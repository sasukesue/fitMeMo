jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { FitnessClassService } from '../service/fitness-class.service';
import { IFitnessClass, FitnessClass } from '../fitness-class.model';

import { FitnessClassUpdateComponent } from './fitness-class-update.component';

describe('Component Tests', () => {
  describe('FitnessClass Management Update Component', () => {
    let comp: FitnessClassUpdateComponent;
    let fixture: ComponentFixture<FitnessClassUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let fitnessClassService: FitnessClassService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [FitnessClassUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(FitnessClassUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(FitnessClassUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      fitnessClassService = TestBed.inject(FitnessClassService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const fitnessClass: IFitnessClass = { id: 456 };

        activatedRoute.data = of({ fitnessClass });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(fitnessClass));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const fitnessClass = { id: 123 };
        spyOn(fitnessClassService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ fitnessClass });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: fitnessClass }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(fitnessClassService.update).toHaveBeenCalledWith(fitnessClass);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const fitnessClass = new FitnessClass();
        spyOn(fitnessClassService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ fitnessClass });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: fitnessClass }));
        saveSubject.complete();

        // THEN
        expect(fitnessClassService.create).toHaveBeenCalledWith(fitnessClass);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const fitnessClass = { id: 123 };
        spyOn(fitnessClassService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ fitnessClass });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(fitnessClassService.update).toHaveBeenCalledWith(fitnessClass);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
