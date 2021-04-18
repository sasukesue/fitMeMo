import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFitnessClass, FitnessClass } from '../fitness-class.model';
import { FitnessClassService } from '../service/fitness-class.service';

@Injectable({ providedIn: 'root' })
export class FitnessClassRoutingResolveService implements Resolve<IFitnessClass> {
  constructor(protected service: FitnessClassService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IFitnessClass> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((fitnessClass: HttpResponse<FitnessClass>) => {
          if (fitnessClass.body) {
            return of(fitnessClass.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new FitnessClass());
  }
}
