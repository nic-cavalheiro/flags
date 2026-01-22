import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Economy1Component } from './economy-1.component';

describe('Economy1Component', () => {
  let component: Economy1Component;
  let fixture: ComponentFixture<Economy1Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Economy1Component]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Economy1Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
