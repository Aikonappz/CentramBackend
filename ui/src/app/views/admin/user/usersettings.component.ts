import { Component, OnInit, ViewChild, } from '@angular/core';
import { UserService } from '../../../service/UserService';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Location } from '@angular/common';
import { MiscService } from '../../../service/MiscService';
import { MustMatch } from '../../../validator/MustMatch';
import { UserDTO } from '../../../model/UserDTO';
import { LoggedInUserService } from '../../../service/LoggedInUserService';

@Component({
  selector: 'app-usersettings',
  templateUrl: './usersettings.component.html',
  styleUrls: ['./usersettings.component.scss']
})
export class UserSettingsComponent implements OnInit {
  moduleName: string = "USER";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  passwordRegex = /^(?=.*\d)(?=.*[!@#$%^&*])(?=.*[a-z])(?=.*[A-Z]).{8,}$/;
  angForm: FormGroup;
  user: UserDTO;
  passwordChanged: boolean = false;
  constructor(
    private loggedInUserService: LoggedInUserService,
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private _location: Location,
    private titleService: Title,
    private router: Router,
    private userService: UserService,
    private miscService: MiscService
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.user = new UserDTO();
  }

  hasPermission(action: string): boolean {
    return this.loggedInUserService.hasPermissionByName(this.moduleName, action);
  }

  getTitle(state, parent) {
    var data = [];
    if (parent && parent.snapshot.data && parent.snapshot.data.title) {
      data.push(parent.snapshot.data.title);
    }
    if (state && parent) {
      data.push(... this.getTitle(state, state.firstChild(parent)));
    }
    return data;
  }

  ngOnInit(): void {
    this.angForm = this.fb.group({
      // oldPassword: new FormControl('', [
      //   Validators.required,
      //   Validators.pattern(this.passwordRegex),
      // ]),
      newPassword: new FormControl('', [
        Validators.required,
        Validators.pattern(this.passwordRegex),
      ]),
      confirmPassword: new FormControl('', [
        Validators.required,
      ]),
    }, {
      validators: MustMatch('newPassword', 'confirmPassword')
    });
  }

  ngAfterViewInit() { }

  ngAfterContentInit() { }

  get f() { return this.angForm.controls; }

  formSubmit() {
    if (this.angForm.valid) {
      //console.log(this.angForm);
      this.user.oldPassword = '';
      this.user.newPassword = btoa(this.angForm.controls['newPassword'].value);
      this.callChangePasswordService();
    } else {
      console.log("Invalid Form!");
    }
  }

  goBack() { this._location.back(); }

  callChangePasswordService() {
    this.userService
      .changePasswordService(this.user)
      .subscribe((data: any) => {
        this.passwordChanged = true;
        //console.log(data);
        //this.router.navigate(['/user']);
      });
  }
}