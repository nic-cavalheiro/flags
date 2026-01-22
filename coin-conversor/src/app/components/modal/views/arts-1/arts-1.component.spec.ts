import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Arts1Component } from './arts-1.component';

describe('Arts1Component', () => {
  let component: Arts1Component;
  let fixture: ComponentFixture<Arts1Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Arts1Component]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Arts1Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
