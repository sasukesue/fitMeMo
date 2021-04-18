import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'location',
        data: { pageTitle: 'Locations' },
        loadChildren: () => import('./location/location.module').then(m => m.LocationModule),
      },
      {
        path: 'fitness-class',
        data: { pageTitle: 'FitnessClasses' },
        loadChildren: () => import('./fitness-class/fitness-class.module').then(m => m.FitnessClassModule),
      },
      {
        path: 'schedule',
        data: { pageTitle: 'Schedules' },
        loadChildren: () => import('./schedule/schedule.module').then(m => m.ScheduleModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
