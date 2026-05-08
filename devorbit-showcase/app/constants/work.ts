import * as THREE from "three";
import { WorkTimelinePoint } from "../types";

export const WORK_TIMELINE: WorkTimelinePoint[] = [
  {
    point: new THREE.Vector3(0, 0, 0),
    year: '2006',
    title: 'Founding of UIT',
    subtitle: 'Established as a specialized University in IT',
    position: 'right',
  },
  {
    point: new THREE.Vector3(-4, -4, -3),
    year: '2011',
    title: 'First Graduation',
    subtitle: 'Nurturing the first generation of IT leaders',
    position: 'left',
  },
  {
    point: new THREE.Vector3(-3, -1, -6),
    year: '2016',
    title: '10-Year Milestone',
    subtitle: 'National excellence in Research and Education',
    position: 'left',
  },
  {
    point: new THREE.Vector3(0, -1, -10),
    year: '2021',
    title: 'AI & Data Science Hub',
    subtitle: 'Leading the digital transformation in Vietnam',
    position: 'left',
  },
  {
    point: new THREE.Vector3(1, 1, -12),
    year: '2026',
    title: '20 Years of Innovation',
    subtitle: 'Shaping the future of technology',
    position: 'right',
  }
]