import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BackofficeReservationsComponent } from './backoffice-reservations.component';

describe('BackofficeReservationsComponent', () => {
  let component: BackofficeReservationsComponent;
  let fixture: ComponentFixture<BackofficeReservationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BackofficeReservationsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BackofficeReservationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
