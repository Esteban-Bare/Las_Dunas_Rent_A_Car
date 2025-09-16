import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BackofficeVehiclesComponent } from './backoffice-vehicles.component';

describe('BackofficeVehiclesComponent', () => {
  let component: BackofficeVehiclesComponent;
  let fixture: ComponentFixture<BackofficeVehiclesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BackofficeVehiclesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BackofficeVehiclesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
