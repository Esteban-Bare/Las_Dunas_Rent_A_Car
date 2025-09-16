import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientRentalsComponent } from './client-rentals.component';

describe('ClientRentalsComponent', () => {
  let component: ClientRentalsComponent;
  let fixture: ComponentFixture<ClientRentalsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientRentalsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClientRentalsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
