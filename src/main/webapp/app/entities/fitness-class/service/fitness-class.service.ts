import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFitnessClass, getFitnessClassIdentifier } from '../fitness-class.model';

export type EntityResponseType = HttpResponse<IFitnessClass>;
export type EntityArrayResponseType = HttpResponse<IFitnessClass[]>;

@Injectable({ providedIn: 'root' })
export class FitnessClassService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/fitness-classes');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(fitnessClass: IFitnessClass): Observable<EntityResponseType> {
    return this.http.post<IFitnessClass>(this.resourceUrl, fitnessClass, { observe: 'response' });
  }

  update(fitnessClass: IFitnessClass): Observable<EntityResponseType> {
    return this.http.put<IFitnessClass>(`${this.resourceUrl}/${getFitnessClassIdentifier(fitnessClass) as number}`, fitnessClass, {
      observe: 'response',
    });
  }

  partialUpdate(fitnessClass: IFitnessClass): Observable<EntityResponseType> {
    return this.http.patch<IFitnessClass>(`${this.resourceUrl}/${getFitnessClassIdentifier(fitnessClass) as number}`, fitnessClass, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IFitnessClass>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFitnessClass[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addFitnessClassToCollectionIfMissing(
    fitnessClassCollection: IFitnessClass[],
    ...fitnessClassesToCheck: (IFitnessClass | null | undefined)[]
  ): IFitnessClass[] {
    const fitnessClasses: IFitnessClass[] = fitnessClassesToCheck.filter(isPresent);
    if (fitnessClasses.length > 0) {
      const fitnessClassCollectionIdentifiers = fitnessClassCollection.map(
        fitnessClassItem => getFitnessClassIdentifier(fitnessClassItem)!
      );
      const fitnessClassesToAdd = fitnessClasses.filter(fitnessClassItem => {
        const fitnessClassIdentifier = getFitnessClassIdentifier(fitnessClassItem);
        if (fitnessClassIdentifier == null || fitnessClassCollectionIdentifiers.includes(fitnessClassIdentifier)) {
          return false;
        }
        fitnessClassCollectionIdentifiers.push(fitnessClassIdentifier);
        return true;
      });
      return [...fitnessClassesToAdd, ...fitnessClassCollection];
    }
    return fitnessClassCollection;
  }
}
