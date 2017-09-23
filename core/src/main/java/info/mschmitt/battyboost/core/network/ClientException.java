package info.mschmitt.battyboost.core.network;

import info.mschmitt.battyboost.core.entities.BattyboostError;

/**
 * @author Matthias Schmitt
 */
public class ClientException extends Exception {
    public ClientException(BattyboostError error) {
        super((String) error.userInfo.get("message"));
    }
}
