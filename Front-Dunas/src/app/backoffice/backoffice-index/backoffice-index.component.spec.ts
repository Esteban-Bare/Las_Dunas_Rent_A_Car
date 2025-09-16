import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BackofficeIndexComponent } from './backoffice-index.component';

describe('BackofficeComponent', () => {
  let component: BackofficeIndexComponent;
  let fixture: ComponentFixture<BackofficeIndexComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BackofficeIndexComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BackofficeIndexComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
