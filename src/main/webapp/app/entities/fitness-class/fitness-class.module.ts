import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { FitnessClassComponent } from './list/fitness-class.component';
import { FitnessClassDetailComponent } from './detail/fitness-class-detail.component';
import { FitnessClassUpdateComponent } from './update/fitness-class-update.component';
import { FitnessClassDeleteDialogComponent } from './delete/fitness-class-delete-dialog.component';
import { FitnessClassRoutingModule } from './route/fitness-class-routing.module';

@NgModule({
  imports: [SharedModule, FitnessClassRoutingModule],
  declarations: [FitnessClassComponent, FitnessClassDetailComponent, FitnessClassUpdateComponent, FitnessClassDeleteDialogComponent],
  entryComponents: [FitnessClassDeleteDialogComponent],
})
export class FitnessClassModule {}
