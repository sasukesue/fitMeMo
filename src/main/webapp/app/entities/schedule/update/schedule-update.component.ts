import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ISchedule, Schedule } from '../schedule.model';
import { ScheduleService } from '../service/schedule.service';
import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';
import { IFitnessClass } from 'app/entities/fitness-class/fitness-class.model';
import { FitnessClassService } from 'app/entities/fitness-class/service/fitness-class.service';

@Component({
  selector: 'jhi-schedule-update',
  templateUrl: './schedule-update.component.html',
})
export class ScheduleUpdateComponent implements OnInit {
  isSaving = false;

  locationsSharedCollection: ILocation[] = [];
  fitnessClassesSharedCollection: IFitnessClass[] = [];

  editForm = this.fb.group({
    id: [],
    dateTime: [null, [Validators.required]],
    availableSlots: [null, [Validators.required]],
    schLoc: [],
    schClass: [],
  });

  constructor(
    protected scheduleService: ScheduleService,
    protected locationService: LocationService,
    protected fitnessClassService: FitnessClassService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ schedule }) => {
      this.updateForm(schedule);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const schedule = this.createFromForm();
    if (schedule.id !== undefined) {
      this.subscribeToSaveResponse(this.scheduleService.update(schedule));
    } else {
      this.subscribeToSaveResponse(this.scheduleService.create(schedule));
    }
  }

  trackLocationById(index: number, item: ILocation): number {
    return item.id!;
  }

  trackFitnessClassById(index: number, item: IFitnessClass): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISchedule>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(schedule: ISchedule): void {
    this.editForm.patchValue({
      id: schedule.id,
      dateTime: schedule.dateTime,
      availableSlots: schedule.availableSlots,
      schLoc: schedule.schLoc,
      schClass: schedule.schClass,
    });

    this.locationsSharedCollection = this.locationService.addLocationToCollectionIfMissing(this.locationsSharedCollection, schedule.schLoc);
    this.fitnessClassesSharedCollection = this.fitnessClassService.addFitnessClassToCollectionIfMissing(
      this.fitnessClassesSharedCollection,
      schedule.schClass
    );
  }

  protected loadRelationshipsOptions(): void {
    this.locationService
      .query()
      .pipe(map((res: HttpResponse<ILocation[]>) => res.body ?? []))
      .pipe(
        map((locations: ILocation[]) =>
          this.locationService.addLocationToCollectionIfMissing(locations, this.editForm.get('schLoc')!.value)
        )
      )
      .subscribe((locations: ILocation[]) => (this.locationsSharedCollection = locations));

    this.fitnessClassService
      .query()
      .pipe(map((res: HttpResponse<IFitnessClass[]>) => res.body ?? []))
      .pipe(
        map((fitnessClasses: IFitnessClass[]) =>
          this.fitnessClassService.addFitnessClassToCollectionIfMissing(fitnessClasses, this.editForm.get('schClass')!.value)
        )
      )
      .subscribe((fitnessClasses: IFitnessClass[]) => (this.fitnessClassesSharedCollection = fitnessClasses));
  }

  protected createFromForm(): ISchedule {
    return {
      ...new Schedule(),
      id: this.editForm.get(['id'])!.value,
      dateTime: this.editForm.get(['dateTime'])!.value,
      availableSlots: this.editForm.get(['availableSlots'])!.value,
      schLoc: this.editForm.get(['schLoc'])!.value,
      schClass: this.editForm.get(['schClass'])!.value,
    };
  }
}
