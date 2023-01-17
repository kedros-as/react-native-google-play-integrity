import {Platform, NativeModules} from 'react-native';

const {RNGooglePlayIntegrity} = NativeModules;

/**
 * Checks if Google Play Integrity API is available
 * @method isPlayIntegrityAvailable
 * @return {Promise}
 */
function isPlayIntegrityAvailable() {
    if (Platform.OS !== 'android') {
        return false;
    }
    return RNGooglePlayIntegrity.isPlayIntegrityAvailable();
}

/**
 * Request an integrity verdict
 * @method requestIntegrityToken
 * @param  {String} nonce Randomly generated nonce
 * @param  {Number} cloudProjectNumber Cloud project number (optional)
 * @return {Promise}
 */
function requestIntegrityToken(nonce, cloudProjectNumber) {
    return RNGooglePlayIntegrity.requestIntegrityToken(nonce, cloudProjectNumber);
}

export default {
    isPlayIntegrityAvailable,
    requestIntegrityToken
};
