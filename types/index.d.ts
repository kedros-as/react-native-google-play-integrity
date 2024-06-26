export declare function isPlayIntegrityAvailable(): Promise<boolean>;

export declare function isStandardIntegrityTokenProviderPrepared(): Promise<boolean>;

export declare function prepareStandardIntegrityTokenProvider(cloudProjectNumber?: `${number}`): Promise<void>;

export declare function requestIntegrityToken(nonce: string, cloudProjectNumber?: `${number}`): Promise<string>;

export declare function requestStandardIntegrityToken(requestHash: string): Promise<string>;
