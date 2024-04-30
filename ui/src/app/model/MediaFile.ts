import { Byte } from "@angular/compiler/src/util";
import { EntityType } from "./enumerator/EntityType";
import { MediaType } from "./enumerator/MediaType";
import { User } from "./User";


export class MediaFile {
    id: number;
    fileName: string;
    fileType: string;
    entityId: number;
    mediaType: MediaType;
    entityType: EntityType;
    content: Byte[];
    user: User;
    uploadedAt: any;
}