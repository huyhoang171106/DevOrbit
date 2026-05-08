import * as THREE from "three";
import { WorkTimelinePoint } from "../types";

export const WORK_TIMELINE: WorkTimelinePoint[] = [
  {
    point: new THREE.Vector3(0, 0, 0),
    year: '1995',
    title: 'Thành lập',
    subtitle: 'Trung tâm Phát triển Công nghệ Thông tin (tin học)',
    position: 'right',
  },
  {
    point: new THREE.Vector3(-4, -4, -3),
    year: '1999',
    title: 'Khoa Công nghệ Thông tin',
    subtitle: 'Trực thuộc Đại học Quốc gia TP.HCM',
    position: 'left',
  },
  {
    point: new THREE.Vector3(-3, -1, -6),
    year: '2006',
    title: 'Trường Đại học Công nghệ Thông tin',
    subtitle: 'Chính thức thành lập theo quyết định của Thủ tướng',
    position: 'left',
  },
  {
    point: new THREE.Vector3(0, -1, -10),
    year: '2010',
    title: 'Cơ sở mới tại Khu Đô thị ĐHQG',
    subtitle: 'Chuyển đến Khu phố 6, P.Linh Trung, TP.Thủ Đức',
    position: 'left',
  },
  {
    point: new THREE.Vector3(1, 1, -12),
    year: '2025',
    title: 'Top trường CNTT hàng đầu',
    subtitle: 'Đào tạo đa ngành: KHMT, KTPM, ATTT, HTTT, KHMT,...',
    position: 'right',
  }
];
