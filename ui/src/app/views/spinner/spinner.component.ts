import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { SpinnerService } from '../../service/SpinnerService';

@Component({
  selector: 'app-spinner',
  templateUrl: './spinner.component.html',
  styleUrls: ['./spinner.component.scss']
})
export class SpinnerComponent implements OnInit {
  public showSpinner = false;
  constructor(
    private spinnerService: SpinnerService,
    private cvRef: ChangeDetectorRef
  ) { }
  ngOnInit(): void { this.init(); }
  init() {
    this.spinnerService.getSpinnerObserver()
      .subscribe((status) => {
        this.showSpinner = status == "start";
        this.cvRef.detectChanges();
      });
  }
}
