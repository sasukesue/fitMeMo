import { ISchedule } from 'app/entities/schedule/schedule.model';

export interface IFitnessClass {
  id?: number;
  className?: string;
  duration?: number;
  level?: string;
  instructorName?: string;
  classSches?: ISchedule[] | null;
}

export class FitnessClass implements IFitnessClass {
  constructor(
    public id?: number,
    public className?: string,
    public duration?: number,
    public level?: string,
    public instructorName?: string,
    public classSches?: ISchedule[] | null
  ) {}
}

export function getFitnessClassIdentifier(fitnessClass: IFitnessClass): number | undefined {
  return fitnessClass.id;
}
