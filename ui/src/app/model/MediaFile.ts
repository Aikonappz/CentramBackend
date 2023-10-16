import { Byte } from "@angular/compiler/src/util";
import { EntityType } from "./enumerator/EntityType";
import { MediaType } from "./enumerator/MediaType";


export class MediaFile {
    id: number;
    fileName: string;
    fileType: string;
    entityId: number;
    mediaType: MediaType;
    entityType: EntityType;
    content: Byte[];
}