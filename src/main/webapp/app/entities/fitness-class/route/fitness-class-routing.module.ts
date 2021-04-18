import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { FitnessClassComponent } from '../list/fitness-class.component';
import { FitnessClassDetailComponent } from '../detail/fitness-class-detail.component';
import { FitnessClassUpdateComponent } from '../update/fitness-class-update.component';
import { FitnessClassRoutingResolveService } from './fitness-class-routing-resolve.service';

const fitnessClassRoute: Routes = [
  {
    path: '',
    component: FitnessClassComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: FitnessClassDetailComponent,
    resolve: {
      fitnessClass: FitnessClassRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: FitnessClassUpdateComponent,
    resolve: {
      fitnessClass: FitnessClassRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: FitnessClassUpdateComponent,
    resolve: {
      fitnessClass: FitnessClassRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(fitnessClassRoute)],
  exports: [RouterModule],
})
export class FitnessClassRoutingModule {}
