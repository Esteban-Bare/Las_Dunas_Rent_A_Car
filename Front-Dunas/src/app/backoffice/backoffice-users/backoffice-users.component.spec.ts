import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BackofficeUsersComponent } from './backoffice-users.component';

describe('BackofficeUsersComponent', () => {
  let component: BackofficeUsersComponent;
  let fixture: ComponentFixture<BackofficeUsersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BackofficeUsersComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BackofficeUsersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
