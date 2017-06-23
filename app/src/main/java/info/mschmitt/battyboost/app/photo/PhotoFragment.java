package info.mschmitt.battyboost.app.photo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import info.mschmitt.battyboost.app.R;
import info.mschmitt.battyboost.app.Router;
import info.mschmitt.battyboost.app.databinding.PhotoViewBinding;
import info.mschmitt.battyboost.core.BattyboostClient;
import info.mschmitt.battyboost.core.utils.firebase.RxAuth;
import info.mschmitt.battyboost.core.utils.firebase.RxStorageReference;
import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;
import java.io.*;
import java.util.UUID;

/**
 * @author Matthias Schmitt
 */
public class PhotoFragment extends Fragment {
    private static final String STATE_VIEW_MODEL = "VIEW_MODEL";
    private static final int RC_PICK_IMAGE = 124;
    private static final StorageMetadata METADATA_JPEG =
            new StorageMetadata.Builder().setContentType("image/jpeg").build();
    public ViewModel viewModel;
    @Inject public Router router;
    @Inject public FirebaseDatabase database;
    @Inject public BattyboostClient client;
    @Inject public FirebaseAuth auth;
    @Inject public FirebaseStorage storage;
    @Inject public AuthUI authUI;
    @Inject public boolean injected;
    private CompositeDisposable compositeDisposable;

    public static Fragment newInstance() {
        return new PhotoFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PICK_IMAGE
                && resultCode == Activity.RESULT_OK
                && data != null
                && data.getData() != null) {
            viewModel.photoUrl = data.getData();
            viewModel.notifyChange();
            Glide.with(this).load(viewModel.photoUrl).into(getBinding().imageView);
        }
    }

    private PhotoViewBinding getBinding() {
        return DataBindingUtil.getBinding(getView());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (!injected) {
            throw new IllegalStateException("Not injected");
        }
        super.onCreate(savedInstanceState);
        viewModel = savedInstanceState == null ? new ViewModel()
                : (ViewModel) savedInstanceState.getSerializable(STATE_VIEW_MODEL);
        if (viewModel.photoUrl == null) {
            FirebaseUser currentUser = auth.getCurrentUser();
            viewModel.photoUrl = currentUser.getPhotoUrl();
            viewModel.notifyChange();
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PhotoViewBinding binding = PhotoViewBinding.inflate(inflater, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Photo");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(0);
        actionBar.setHomeActionContentDescription(0);
        binding.setFragment(this);
        Glide.with(this).load(viewModel.photoUrl).into(binding.imageView);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_VIEW_MODEL, viewModel);
    }

    @Override
    public void onPause() {
        compositeDisposable.dispose();
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.photo, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_item_save);
        menuItem.setOnMenuItemClickListener(this::onSaveMenuItemClick);
    }

    private ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    private boolean onSaveMenuItemClick(MenuItem menuItem) {
        if (viewModel.photoUrl == null) {
            return false;
        }
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), viewModel.photoUrl);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] bytes = baos.toByteArray();
            uploadPhoto(bytes);
        } catch (IOException e) {
            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void uploadPhoto(byte[] bytes) throws IOException {
        FirebaseUser currentUser = auth.getCurrentUser();
        Uri oldPhotoUrl = currentUser.getPhotoUrl();
        StorageReference oldPhotoRef = oldPhotoUrl == null ? null : storage.getReferenceFromUrl(oldPhotoUrl.toString());
        StorageReference photoRef =
                client.usersStorageRef.child(currentUser.getUid()).child(UUID.randomUUID().toString() + ".jpg");
        RxStorageReference.Upload upload = RxStorageReference.putBytes(photoRef, bytes, METADATA_JPEG);
        ProgressDialog progressDialog = new ProgressDialog(getView().getContext());
        progressDialog.setMax(100);
        progressDialog.setMessage("Uploading...");
        Disposable uploadDisposable = upload.events.filter(event -> event.inProgress).subscribe(event -> {
            double progressPercent = (100.0 * event.bytesTransferred) / event.totalByteCount;
            progressDialog.setProgress((int) progressPercent);
        }, throwable -> {});
        compositeDisposable.add(uploadDisposable);
        Disposable disposable = upload.events.filter(event -> event.successful)
                .flatMapCompletable(event -> RxAuth.updateProfile(auth,
                        new UserProfileChangeRequest.Builder().setPhotoUri(event.downloadUrl).build()))
                .andThen(oldPhotoRef != null ? RxStorageReference.delete(oldPhotoRef) : Completable.complete())
                .subscribe(() -> {
                    progressDialog.dismiss();
                    router.goBack(this);
                }, throwable -> progressDialog.dismiss());
        compositeDisposable.add(disposable);
        progressDialog.setOnCancelListener(dialog -> {
            uploadDisposable.dispose();
            disposable.dispose();
            upload.cancel();
        });
        progressDialog.show();
        upload.start();
    }

    public void onPickPhotoClick() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RC_PICK_IMAGE);
    }

    public void goUp() {
        router.goUp(this);
    }

    public static class ViewModel extends BaseObservable implements Serializable {
        @Bindable public transient Uri photoUrl;

        private void writeObject(ObjectOutputStream oos) throws IOException {
            oos.defaultWriteObject();
            oos.writeObject(photoUrl == null ? null : photoUrl.toString());
        }

        private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
            ois.defaultReadObject();
            String uriString = (String) ois.readObject();
            photoUrl = Uri.parse(uriString);
        }
    }
}
