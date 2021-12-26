import { LicenseType } from "./enumerator/LicenseType";
import { MediaFile } from "./MediaFile";
import { Permission } from "./Permssion";

export class LoggedInUser {
    userId: number;
    organisationId: number;
    appManager: boolean;
    email: string;
    jwtToken: string;
    timeZone: string;
    location: string;
    department: string;
    organisationLogo: MediaFile;
    profileImage: MediaFile;
    orgName: string;
    name: string;
    roles: string[];
    modulePermissions: Permission[];
    licenseType: LicenseType;
}