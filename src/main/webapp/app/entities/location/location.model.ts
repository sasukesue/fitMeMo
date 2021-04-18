import { ISchedule } from 'app/entities/schedule/schedule.model';

export interface ILocation {
  id?: number;
  branchName?: string;
  address?: string;
  locSches?: ISchedule[] | null;
}

export class Location implements ILocation {
  constructor(public id?: number, public branchName?: string, public address?: string, public locSches?: ISchedule[] | null) {}
}

export function getLocationIdentifier(location: ILocation): number | undefined {
  return location.id;
}
