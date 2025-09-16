import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BackofficeRentalsComponent } from './backoffice-rentals.component';

describe('BackofficeRentalsComponent', () => {
  let component: BackofficeRentalsComponent;
  let fixture: ComponentFixture<BackofficeRentalsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BackofficeRentalsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BackofficeRentalsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
