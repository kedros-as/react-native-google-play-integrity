export declare function isPlayIntegrityAvailable(): Promise<boolean>;

export declare function requestIntegrityToken(nonce: string, cloudProjectNumber?: `${number}`): Promise<string>;
