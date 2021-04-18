import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IFitnessClass, FitnessClass } from '../fitness-class.model';

import { FitnessClassService } from './fitness-class.service';

describe('Service Tests', () => {
  describe('FitnessClass Service', () => {
    let service: FitnessClassService;
    let httpMock: HttpTestingController;
    let elemDefault: IFitnessClass;
    let expectedResult: IFitnessClass | IFitnessClass[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(FitnessClassService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        className: 'AAAAAAA',
        duration: 0,
        level: 'AAAAAAA',
        instructorName: 'AAAAAAA',
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a FitnessClass', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new FitnessClass()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a FitnessClass', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            className: 'BBBBBB',
            duration: 1,
            level: 'BBBBBB',
            instructorName: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a FitnessClass', () => {
        const patchObject = Object.assign(
          {
            duration: 1,
          },
          new FitnessClass()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of FitnessClass', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            className: 'BBBBBB',
            duration: 1,
            level: 'BBBBBB',
            instructorName: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a FitnessClass', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addFitnessClassToCollectionIfMissing', () => {
        it('should add a FitnessClass to an empty array', () => {
          const fitnessClass: IFitnessClass = { id: 123 };
          expectedResult = service.addFitnessClassToCollectionIfMissing([], fitnessClass);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(fitnessClass);
        });

        it('should not add a FitnessClass to an array that contains it', () => {
          const fitnessClass: IFitnessClass = { id: 123 };
          const fitnessClassCollection: IFitnessClass[] = [
            {
              ...fitnessClass,
            },
            { id: 456 },
          ];
          expectedResult = service.addFitnessClassToCollectionIfMissing(fitnessClassCollection, fitnessClass);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a FitnessClass to an array that doesn't contain it", () => {
          const fitnessClass: IFitnessClass = { id: 123 };
          const fitnessClassCollection: IFitnessClass[] = [{ id: 456 }];
          expectedResult = service.addFitnessClassToCollectionIfMissing(fitnessClassCollection, fitnessClass);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(fitnessClass);
        });

        it('should add only unique FitnessClass to an array', () => {
          const fitnessClassArray: IFitnessClass[] = [{ id: 123 }, { id: 456 }, { id: 98257 }];
          const fitnessClassCollection: IFitnessClass[] = [{ id: 123 }];
          expectedResult = service.addFitnessClassToCollectionIfMissing(fitnessClassCollection, ...fitnessClassArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const fitnessClass: IFitnessClass = { id: 123 };
          const fitnessClass2: IFitnessClass = { id: 456 };
          expectedResult = service.addFitnessClassToCollectionIfMissing([], fitnessClass, fitnessClass2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(fitnessClass);
          expect(expectedResult).toContain(fitnessClass2);
        });

        it('should accept null and undefined values', () => {
          const fitnessClass: IFitnessClass = { id: 123 };
          expectedResult = service.addFitnessClassToCollectionIfMissing([], null, fitnessClass, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(fitnessClass);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
