import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { AuthRequest } from '../../model/AuthRequest';
import { CommonResponse } from '../../model/CommonResponse';
import { UserService } from '../../service/UserService';
import { MustMatch } from '../../validator/MustMatch';

@Component({
  selector: 'app-dashboard',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {
  passwordRegex = /^(?=.*\d)(?=.*[!@#$%^&*])(?=.*[a-z])(?=.*[A-Z]).{8,}$/;
  resetId: string = '';
  authRequest: AuthRequest;
  angForm: FormGroup;

  constructor(titleService: Title, private router: Router, private route: ActivatedRoute, private fb: FormBuilder, private userService: UserService) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        //console.log('title', title);
        titleService.setTitle(title);
      }
    });
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
    if (!this.route.snapshot.paramMap.has('id')) {
      this.router.navigate(['/']);
    }
    this.resetId = this.route.snapshot.paramMap.get('id') || '';
    this.angForm = this.fb.group({
      password: new FormControl('', [
        Validators.required,
        Validators.pattern(this.passwordRegex),
      ]),
      confirmPassword: new FormControl('', [
        Validators.required,
        Validators.pattern(this.passwordRegex),
      ]),
    }, {
      validators: MustMatch('password', 'confirmPassword')
    });
    this.authRequest = new AuthRequest();
  }

  formSubmit() {
    if (this.angForm.valid) {
      //console.log(this.angForm);
      this.authRequest.username = this.resetId;
      this.authRequest.password = this.angForm.controls['password'].value;
      console.log(this.authRequest);
      this.callResetPasswordService();
    } else {
      console.log("Invalid Form!");
    }
  }

  get f() {
    return this.angForm.controls;
  }

  callResetPasswordService() {
    this.userService
      .resetPasswordService(
        this.authRequest
      )
      .subscribe((data: CommonResponse) => {
        //console.log(data);
        //this.angForm.reset();
        //this.toggleStockAddMode();
        this.router.navigate(['/']);
      });
  }

}

