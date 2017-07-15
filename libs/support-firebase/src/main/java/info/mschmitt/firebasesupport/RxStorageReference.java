package info.mschmitt.firebasesupport;

import android.net.Uri;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.*;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * @author Matthias Schmitt
 */
public class RxStorageReference {
    public static Upload putBytes(StorageReference reference, byte[] bytes, StorageMetadata metadata) {
        return new Upload() {
            @Override
            protected UploadTask startTask() {
                return reference.putBytes(bytes, metadata);
            }
        };
    }

    public static Upload putFile(StorageReference reference, Uri uri, StorageMetadata metadata) {
        return new Upload() {
            @Override
            protected UploadTask startTask() {
                return reference.putFile(uri, metadata);
            }
        };
    }

    public static Completable delete(StorageReference reference) {
        return Completable.create(e -> reference.delete()
                .addOnSuccessListener(ignore -> e.onComplete())
                .addOnFailureListener(e::onError));
    }

    public abstract static class Upload {
        public UploadTask task;
        private PublishSubject<UploadEvent> subject = PublishSubject.create();
        public Observable<UploadEvent> events = subject;

        private Upload() {
        }

        public void start() {
            task = startTask();
            OnProgressListener<? super UploadTask.TaskSnapshot> onProgressListener =
                    snapshot -> subject.onNext(new UploadEvent(snapshot));
            OnPausedListener<? super UploadTask.TaskSnapshot> onPausedListener =
                    snapshot -> subject.onNext(new UploadEvent(snapshot));
            OnSuccessListener<? super UploadTask.TaskSnapshot> onSuccessListener =
                    snapshot -> subject.onNext(new UploadEvent(snapshot));
            OnFailureListener onFailureListener = subject::onError;
            OnCompleteListener<UploadTask.TaskSnapshot> onCompleteListener = task -> subject.onComplete();
            task.addOnProgressListener(onProgressListener)
                    .addOnPausedListener(onPausedListener)
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(onFailureListener)
                    .addOnCompleteListener(onCompleteListener);
        }

        protected abstract UploadTask startTask();

        public boolean cancel() {
            return task.cancel();
        }
    }

    public static class UploadEvent {
        public final boolean inProgress;
        public final boolean successful;
        public final long bytesTransferred;
        public final long totalByteCount;
        public final Uri downloadUrl;

        private UploadEvent(UploadTask.TaskSnapshot snapshot) {
            inProgress = snapshot.getTask().isInProgress();
            successful = snapshot.getTask().isSuccessful();
            bytesTransferred = snapshot.getBytesTransferred();
            totalByteCount = snapshot.getTotalByteCount();
            downloadUrl = snapshot.getDownloadUrl();
        }
    }
}
