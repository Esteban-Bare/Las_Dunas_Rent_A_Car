import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BackofficePaymentsComponent } from './backoffice-payments.component';

describe('BackofficePaymentsComponent', () => {
  let component: BackofficePaymentsComponent;
  let fixture: ComponentFixture<BackofficePaymentsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BackofficePaymentsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BackofficePaymentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
