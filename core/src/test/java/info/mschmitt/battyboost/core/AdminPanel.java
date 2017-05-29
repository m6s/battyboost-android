package info.mschmitt.battyboost.core;

import org.junit.Test;

import java.net.URLEncoder;
import java.util.UUID;

/**
 * @author Matthias Schmitt
 */
public class AdminPanel {
//    @Test
//    public void registerPartner() {
//    }

    @Test
    public void printPowerbankQRs() throws Exception {
        String qrData = createPowerbankQRData();
        String url =
                "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + URLEncoder.encode(qrData, "UTF-8");
        System.out.println(url);
    }

    private String createPowerbankQRData() throws Exception {
        String version = "0";
        String target = "0";
        UUID uuid = UUID.randomUUID();
        return version + target + uuid.toString();
    }
}
