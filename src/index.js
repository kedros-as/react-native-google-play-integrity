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
 * Checks if Standard Integrity Token Provider is prepared
 * @method isStandardIntegrityTokenProviderPrepared
 * @return {Promise}
 */
function isStandardIntegrityTokenProviderPrepared() {
    return RNGooglePlayIntegrity.isStandardIntegrityTokenProviderPrepared();
}

/**
 * Prepare Standard Integrity Token Provider
 * @method prepareStandardIntegrityTokenProvider
 * @param  {Number} cloudProjectNumber Cloud project number (optional)
 * @return {Promise}
 */
function prepareStandardIntegrityTokenProvider(cloudProjectNumber) {
    return RNGooglePlayIntegrity.prepareStandardIntegrityTokenProvider(cloudProjectNumber);
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

/**
 * Request an integrity verdict
 * @method requestIntegrityToken
 * @param  {String} requestHash Randomly generated hash
 * @return {Promise}
 */
function requestStandardIntegrityToken(requestHash) {
    return RNGooglePlayIntegrity.requestStandardIntegrityToken(requestHash);
}

export default {
    isPlayIntegrityAvailable,
    isStandardIntegrityTokenProviderPrepared,
    prepareStandardIntegrityTokenProvider,
    requestIntegrityToken,
    requestStandardIntegrityToken
};
