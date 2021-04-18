import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { FitnessClassDetailComponent } from './fitness-class-detail.component';

describe('Component Tests', () => {
  describe('FitnessClass Management Detail Component', () => {
    let comp: FitnessClassDetailComponent;
    let fixture: ComponentFixture<FitnessClassDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [FitnessClassDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ fitnessClass: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(FitnessClassDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(FitnessClassDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load fitnessClass on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.fitnessClass).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
