import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IFitnessClass } from '../fitness-class.model';
import { FitnessClassService } from '../service/fitness-class.service';

@Component({
  templateUrl: './fitness-class-delete-dialog.component.html',
})
export class FitnessClassDeleteDialogComponent {
  fitnessClass?: IFitnessClass;

  constructor(protected fitnessClassService: FitnessClassService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.fitnessClassService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
