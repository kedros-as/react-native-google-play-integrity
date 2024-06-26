package sk.kedros.playintegrity;

import androidx.annotation.NonNull;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.integrity.IntegrityManager;
import com.google.android.play.core.integrity.IntegrityManagerFactory;
import com.google.android.play.core.integrity.IntegrityServiceException;
import com.google.android.play.core.integrity.IntegrityTokenRequest;
import com.google.android.play.core.integrity.IntegrityTokenResponse;
import com.google.android.play.core.integrity.StandardIntegrityManager;

public class RNGooglePlayIntegrityModule extends ReactContextBaseJavaModule {

    private static final String UNEXPECTED_ERROR_CODE = "-255";

    private final ReactApplicationContext reactContext;
    private final ReactApplicationContext baseContext;

    private volatile StandardIntegrityManager.StandardIntegrityTokenProvider standardIntegrityTokenProvider;

    public RNGooglePlayIntegrityModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.baseContext = getReactApplicationContext();
    }

    @Override
    public String getName() {
        return "RNGooglePlayIntegrity";
    }

    /**
     * Checks if Google Play Integrity API is available
     * See https://developer.android.com/google/play/integrity/overview
     *
     * @param promise
     */
    @ReactMethod
    public void isPlayIntegrityAvailable(final Promise promise) {
        boolean apiAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(baseContext) == ConnectionResult.SUCCESS;
        promise.resolve(apiAvailable);
    }

    /**
     * Checks if Standard Integrity Token Provider is prepared
     * See https://developer.android.com/google/play/integrity/standard#prepare-integrity
     *
     * @param promise
     */
    @ReactMethod
    public void isStandardIntegrityTokenProviderPrepared(final Promise promise) {
        promise.resolve(standardIntegrityTokenProvider != null);
    }

    /**
     * Prepare Standard Integrity Token Provider
     * See https://developer.android.com/google/play/integrity/standard#prepare-integrity
     *
     * @param cloudProjectNumber
     * @param promise
     */
    @ReactMethod
    public void prepareStandardIntegrityTokenProvider(
            final String cloudProjectNumber,
            final Promise promise
    ) {
        try {

            StandardIntegrityManager standardIntegrityManager = IntegrityManagerFactory.createStandard(baseContext);

            StandardIntegrityManager.PrepareIntegrityTokenRequest.Builder request = StandardIntegrityManager.PrepareIntegrityTokenRequest.builder();

            if (cloudProjectNumber != null && !cloudProjectNumber.isEmpty()) {
                request.setCloudProjectNumber(Long.parseLong(cloudProjectNumber));
            }

            Task<StandardIntegrityManager.StandardIntegrityTokenProvider> response =
                    standardIntegrityManager.prepareIntegrityToken(request.build());

            response.addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    promise.reject(UNEXPECTED_ERROR_CODE, "Canceled");
                }
            });

            response.addOnSuccessListener(new OnSuccessListener<StandardIntegrityManager.StandardIntegrityTokenProvider>() {
                @Override
                public void onSuccess(@NonNull StandardIntegrityManager.StandardIntegrityTokenProvider tokenProvider) {
                    standardIntegrityTokenProvider = tokenProvider;
                    promise.resolve(null);
                }
            });

            response.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof IntegrityServiceException) {
                        IntegrityServiceException error = (IntegrityServiceException) e;
                        promise.reject(String.valueOf(error.getErrorCode()), error);
                    } else {
                        promise.reject(UNEXPECTED_ERROR_CODE, e);
                    }
                }
            });

        } catch (Throwable e) {
            promise.reject(UNEXPECTED_ERROR_CODE, e);
        }

    }

    /**
     * Request an integrity verdict
     * See https://developer.android.com/google/play/integrity/verdict#request
     *
     * @param nonce
     * @param cloudProjectNumber
     * @param promise
     */
    @ReactMethod
    public void requestIntegrityToken(
            final String nonce,
            final String cloudProjectNumber,
            final Promise promise) {

        try {

            IntegrityManager integrityManager =
                    IntegrityManagerFactory.create(baseContext);

            IntegrityTokenRequest.Builder request = IntegrityTokenRequest.builder()
                    .setNonce(nonce);

            if (cloudProjectNumber != null && !cloudProjectNumber.isEmpty()) {
                request.setCloudProjectNumber(Long.parseLong(cloudProjectNumber));
            }

            Task<IntegrityTokenResponse> integrityTokenResponse =
                    integrityManager
                            .requestIntegrityToken(request.build());

            integrityTokenResponse.addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    promise.reject(UNEXPECTED_ERROR_CODE, "Canceled");
                }
            });

            integrityTokenResponse.addOnSuccessListener(new OnSuccessListener<IntegrityTokenResponse>() {
                @Override
                public void onSuccess(@NonNull IntegrityTokenResponse response) {
                    promise.resolve(response.token());
                }
            });

            integrityTokenResponse.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof IntegrityServiceException) {
                        IntegrityServiceException error = (IntegrityServiceException) e;
                        promise.reject(String.valueOf(error.getErrorCode()), error);
                    } else {
                        promise.reject(UNEXPECTED_ERROR_CODE, e);
                    }
                }
            });

        } catch (Throwable e) {
            promise.reject(UNEXPECTED_ERROR_CODE, e);
        }

    }

    /**
     * Request an integrity verdict
     * See https://developer.android.com/google/play/integrity/standard#request-integrity
     *
     * @param requestHash
     * @param promise
     */
    @ReactMethod
    public void requestStandardIntegrityToken(
            final String requestHash,
            final Promise promise) {

        try {

            if (standardIntegrityTokenProvider == null) {
                promise.reject(UNEXPECTED_ERROR_CODE, "Integrity token provider not prepared");
                return;
            }

            StandardIntegrityManager.StandardIntegrityTokenRequest request = StandardIntegrityManager.StandardIntegrityTokenRequest.builder()
                    .setRequestHash(requestHash)
                    .build();

            Task<StandardIntegrityManager.StandardIntegrityToken> integrityTokenResponse =
                    standardIntegrityTokenProvider.request(request);

            integrityTokenResponse.addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    promise.reject(UNEXPECTED_ERROR_CODE, "Canceled");
                }
            });

            integrityTokenResponse.addOnSuccessListener(new OnSuccessListener<StandardIntegrityManager.StandardIntegrityToken>() {
                @Override
                public void onSuccess(@NonNull StandardIntegrityManager.StandardIntegrityToken response) {
                    promise.resolve(response.token());
                }
            });

            integrityTokenResponse.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof IntegrityServiceException) {
                        IntegrityServiceException error = (IntegrityServiceException) e;
                        promise.reject(String.valueOf(error.getErrorCode()), error);
                    } else {
                        promise.reject(UNEXPECTED_ERROR_CODE, e);
                    }
                }
            });

        } catch (Throwable e) {
            promise.reject(UNEXPECTED_ERROR_CODE, e);
        }

    }
}
