jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ScheduleService } from '../service/schedule.service';
import { ISchedule, Schedule } from '../schedule.model';
import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';
import { IFitnessClass } from 'app/entities/fitness-class/fitness-class.model';
import { FitnessClassService } from 'app/entities/fitness-class/service/fitness-class.service';

import { ScheduleUpdateComponent } from './schedule-update.component';

describe('Component Tests', () => {
  describe('Schedule Management Update Component', () => {
    let comp: ScheduleUpdateComponent;
    let fixture: ComponentFixture<ScheduleUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let scheduleService: ScheduleService;
    let locationService: LocationService;
    let fitnessClassService: FitnessClassService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ScheduleUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ScheduleUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ScheduleUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      scheduleService = TestBed.inject(ScheduleService);
      locationService = TestBed.inject(LocationService);
      fitnessClassService = TestBed.inject(FitnessClassService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Location query and add missing value', () => {
        const schedule: ISchedule = { id: 456 };
        const schLoc: ILocation = { id: 37955 };
        schedule.schLoc = schLoc;

        const locationCollection: ILocation[] = [{ id: 96203 }];
        spyOn(locationService, 'query').and.returnValue(of(new HttpResponse({ body: locationCollection })));
        const additionalLocations = [schLoc];
        const expectedCollection: ILocation[] = [...additionalLocations, ...locationCollection];
        spyOn(locationService, 'addLocationToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ schedule });
        comp.ngOnInit();

        expect(locationService.query).toHaveBeenCalled();
        expect(locationService.addLocationToCollectionIfMissing).toHaveBeenCalledWith(locationCollection, ...additionalLocations);
        expect(comp.locationsSharedCollection).toEqual(expectedCollection);
      });

      it('Should call FitnessClass query and add missing value', () => {
        const schedule: ISchedule = { id: 456 };
        const schClass: IFitnessClass = { id: 73348 };
        schedule.schClass = schClass;

        const fitnessClassCollection: IFitnessClass[] = [{ id: 88663 }];
        spyOn(fitnessClassService, 'query').and.returnValue(of(new HttpResponse({ body: fitnessClassCollection })));
        const additionalFitnessClasses = [schClass];
        const expectedCollection: IFitnessClass[] = [...additionalFitnessClasses, ...fitnessClassCollection];
        spyOn(fitnessClassService, 'addFitnessClassToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ schedule });
        comp.ngOnInit();

        expect(fitnessClassService.query).toHaveBeenCalled();
        expect(fitnessClassService.addFitnessClassToCollectionIfMissing).toHaveBeenCalledWith(
          fitnessClassCollection,
          ...additionalFitnessClasses
        );
        expect(comp.fitnessClassesSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const schedule: ISchedule = { id: 456 };
        const schLoc: ILocation = { id: 3491 };
        schedule.schLoc = schLoc;
        const schClass: IFitnessClass = { id: 22611 };
        schedule.schClass = schClass;

        activatedRoute.data = of({ schedule });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(schedule));
        expect(comp.locationsSharedCollection).toContain(schLoc);
        expect(comp.fitnessClassesSharedCollection).toContain(schClass);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const schedule = { id: 123 };
        spyOn(scheduleService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ schedule });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: schedule }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(scheduleService.update).toHaveBeenCalledWith(schedule);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const schedule = new Schedule();
        spyOn(scheduleService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ schedule });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: schedule }));
        saveSubject.complete();

        // THEN
        expect(scheduleService.create).toHaveBeenCalledWith(schedule);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const schedule = { id: 123 };
        spyOn(scheduleService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ schedule });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(scheduleService.update).toHaveBeenCalledWith(schedule);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackLocationById', () => {
        it('Should return tracked Location primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackLocationById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackFitnessClassById', () => {
        it('Should return tracked FitnessClass primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackFitnessClassById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
