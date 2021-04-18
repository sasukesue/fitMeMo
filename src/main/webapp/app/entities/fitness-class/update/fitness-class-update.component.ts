import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IFitnessClass, FitnessClass } from '../fitness-class.model';
import { FitnessClassService } from '../service/fitness-class.service';

@Component({
  selector: 'jhi-fitness-class-update',
  templateUrl: './fitness-class-update.component.html',
})
export class FitnessClassUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    className: [null, [Validators.required]],
    duration: [null, [Validators.required]],
    level: [null, [Validators.required]],
    instructorName: [null, [Validators.required]],
  });

  constructor(protected fitnessClassService: FitnessClassService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ fitnessClass }) => {
      this.updateForm(fitnessClass);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const fitnessClass = this.createFromForm();
    if (fitnessClass.id !== undefined) {
      this.subscribeToSaveResponse(this.fitnessClassService.update(fitnessClass));
    } else {
      this.subscribeToSaveResponse(this.fitnessClassService.create(fitnessClass));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFitnessClass>>): void {
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

  protected updateForm(fitnessClass: IFitnessClass): void {
    this.editForm.patchValue({
      id: fitnessClass.id,
      className: fitnessClass.className,
      duration: fitnessClass.duration,
      level: fitnessClass.level,
      instructorName: fitnessClass.instructorName,
    });
  }

  protected createFromForm(): IFitnessClass {
    return {
      ...new FitnessClass(),
      id: this.editForm.get(['id'])!.value,
      className: this.editForm.get(['className'])!.value,
      duration: this.editForm.get(['duration'])!.value,
      level: this.editForm.get(['level'])!.value,
      instructorName: this.editForm.get(['instructorName'])!.value,
    };
  }
}
