import { AssetRequest } from "./AssetRequest";

export interface IAllocateAsset {
    assetRequest: AssetRequest;
    allocate: boolean;
}