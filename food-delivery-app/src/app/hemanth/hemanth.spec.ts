import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Hemanth } from './hemanth';

describe('Hemanth', () => {
  let component: Hemanth;
  let fixture: ComponentFixture<Hemanth>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Hemanth]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Hemanth);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
