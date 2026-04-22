import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Annie } from './annie';

describe('Annie', () => {
  let component: Annie;
  let fixture: ComponentFixture<Annie>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Annie]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Annie);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
