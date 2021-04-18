jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IFitnessClass, FitnessClass } from '../fitness-class.model';
import { FitnessClassService } from '../service/fitness-class.service';

import { FitnessClassRoutingResolveService } from './fitness-class-routing-resolve.service';

describe('Service Tests', () => {
  describe('FitnessClass routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: FitnessClassRoutingResolveService;
    let service: FitnessClassService;
    let resultFitnessClass: IFitnessClass | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(FitnessClassRoutingResolveService);
      service = TestBed.inject(FitnessClassService);
      resultFitnessClass = undefined;
    });

    describe('resolve', () => {
      it('should return IFitnessClass returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultFitnessClass = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultFitnessClass).toEqual({ id: 123 });
      });

      it('should return new IFitnessClass if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultFitnessClass = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultFitnessClass).toEqual(new FitnessClass());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultFitnessClass = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultFitnessClass).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
