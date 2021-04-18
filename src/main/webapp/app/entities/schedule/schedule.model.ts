import * as dayjs from 'dayjs';
import { ILocation } from 'app/entities/location/location.model';
import { IFitnessClass } from 'app/entities/fitness-class/fitness-class.model';

export interface ISchedule {
  id?: number;
  dateTime?: dayjs.Dayjs;
  availableSlots?: number;
  schLoc?: ILocation | null;
  schClass?: IFitnessClass | null;
}

export class Schedule implements ISchedule {
  constructor(
    public id?: number,
    public dateTime?: dayjs.Dayjs,
    public availableSlots?: number,
    public schLoc?: ILocation | null,
    public schClass?: IFitnessClass | null
  ) {}
}

export function getScheduleIdentifier(schedule: ISchedule): number | undefined {
  return schedule.id;
}
