import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Wiki1Component } from './wiki-1.component';

describe('Wiki1Component', () => {
  let component: Wiki1Component;
  let fixture: ComponentFixture<Wiki1Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Wiki1Component]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Wiki1Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
