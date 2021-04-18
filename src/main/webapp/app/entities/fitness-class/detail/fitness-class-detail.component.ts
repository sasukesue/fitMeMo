import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IFitnessClass } from '../fitness-class.model';

@Component({
  selector: 'jhi-fitness-class-detail',
  templateUrl: './fitness-class-detail.component.html',
})
export class FitnessClassDetailComponent implements OnInit {
  fitnessClass: IFitnessClass | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ fitnessClass }) => {
      this.fitnessClass = fitnessClass;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
