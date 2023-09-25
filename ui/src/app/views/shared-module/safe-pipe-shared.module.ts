import { NgModule, } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SafePipe } from '../../service/pipe/SafePipe';



@NgModule({
    imports: [
    ],
    declarations: [
        SafePipe
    ],
    exports: [
        SafePipe
    ]
})

export class SafePipeSharedModule { }