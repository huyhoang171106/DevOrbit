# DevOrbit Web Showcase Content

## 1. Mo dau

Xin chao thay co va cac ban. Hom nay nhom em xin gioi thieu DevOrbit, mot nen tang giup sinh vien UIT kham pha mon hoc, tim repository hoc thuat, xem so do kien thuc chuong trinh dao tao, tao anh ky niem va nhan goi y lo trinh hoc tap bang AI.

Diem chinh cua DevOrbit la gom nhieu nhu cau hoc tap vao mot web app duy nhat: tra cuu mon hoc, xem ma nguon tham khao, hieu quan he tien quyet giua cac mon, luu tai nguyen can hoc, va ho tro quan tri du lieu repository tu GitHub.

## 2. Thong diep chinh

DevOrbit bien viec tim tai lieu va ma nguon hoc thuat thanh mot trai nghiem co cau truc.

Thay vi sinh vien tu tim tung repo tren GitHub, DevOrbit gom cac repo theo mon hoc, hien thi thong tin quan trong, loc theo tech stack, va bo sung AI summary de nguoi hoc nam nhanh noi dung repo.

Thay vi xem chuong trinh dao tao bang bang tinh khong tuong tac, DevOrbit truc quan hoa lo trinh hoc bang knowledge graph va bang ke hoach hoc ky, giup sinh vien thay duoc mon nao nen hoc truoc, mon nao bi anh huong khi truot, va nhom mon tu chon nao can hoan thanh.

## 3. Demo Flow Tong Quan

### Buoc 1: Trang chu

Mo trang chu DevOrbit.

Noi dung thuyet trinh:

DevOrbit bat dau bang trang chu danh cho sinh vien. Trang nay gioi thieu nhanh cac gia tri chinh: kham pha mon hoc, ket noi voi repository, va xay dung lo trinh hoc tap. Discovery Feed tren trang chu giup nguoi dung thay cac repository moi hoac noi bat ngay tu dau.

Can nhan manh:

- Web duoc xay bang React 19, TypeScript, Vite va Tailwind.
- Du lieu hien thi den tu backend Spring Boot thong qua REST API.
- Trai nghiem duoc toi uu cho viec kham pha, khong chi la mot danh sach tinh.

### Buoc 2: Danh sach mon hoc

Mo `/courses`.

Noi dung thuyet trinh:

Day la catalogue mon hoc. Sinh vien co the xem toan bo mon hoc dang hoat dong, tim theo ten hoac ma mon, va sap xep theo so luong repository lien quan. Moi card mon hoc hien thi cac thong tin can ra quyet dinh nhanh: ma mon, ten mon, so tin chi va so repository.

Gia tri:

- Sinh vien bat dau tu mon hoc, khong phai tu mot repo roi doan xem no thuoc mon nao.
- Tim kiem nhanh giup tiet kiem thoi gian khi catalogue lon.
- So repository tren moi mon cho thay mon nao dang co nhieu tai nguyen tham khao.

### Buoc 3: Chi tiet mon hoc

Mo mot mon bat ky trong `/courses/:courseId`.

Noi dung thuyet trinh:

Trang chi tiet mon hoc la noi gom tat ca tai nguyen lien quan. Sinh vien xem mo ta mon, tin chi, loai hoc phan, repository lien quan, playlist YouTube, bai viet va tutorial neu co. Phan repository co bo loc theo tech stack de nguoi hoc chon dung cong nghe minh can.

Gia tri:

- Mot mon hoc khong chi co thong tin mo ta, ma co ca nguon hoc thuc te.
- Bo loc tech stack giup sinh vien tim repo phu hop voi framework minh dang hoc.
- Chuc nang bookmark giup luu mon hoc hoac repo de xem lai sau.

### Buoc 4: Chi tiet repository

Mo `/repos/:repoId`.

Noi dung thuyet trinh:

Trang repository hien thi thong tin can thiet truoc khi sinh vien mo GitHub: ten repo, mo ta, ngon ngu chinh, tech stack, stars va nut truy cap ma nguon. Diem quan trong la DevOrbit co AI Summary va AI Advice. AI tom tat repo de nguoi hoc hieu nhanh repo nay lam gi, sau do dua ra loi khuyen hoc tap de biet nen doc phan nao truoc.

Gia tri:

- Giam thoi gian doc repo lan dau.
- Bien repository thanh tai nguyen hoc tap co huong dan.
- Phu hop voi sinh vien moi tiep can mot mon hoac mot cong nghe moi.

### Buoc 5: Knowledge Graph va lap ke hoach hoc tap

Mo `/knowledge-graph`.

Noi dung thuyet trinh:

Day la phan truc quan hoa chuong trinh dao tao. DevOrbit hien thi cac mon hoc theo 8 hoc ky, quan he tien quyet, cac nhom tu chon va cac mon tot nghiep. Sinh vien co the keo tha mon hoc trong bang ke hoach, xem canh bao khi thieu dieu kien tien quyet, theo doi tin chi, va dieu chinh dau vao tieng Anh.

Gia tri:

- Sinh vien thay duoc tac dong cua viec sap xep mon hoc.
- He thong giup tranh lap ke hoach sai tien quyet.
- Cac nhom tu chon va lo trinh tot nghiep duoc dua vao cung mot man hinh.

Neu can demo ngan:

1. Chon mot mon co tien quyet.
2. Thu sap xep khi chua co mon nen.
3. Cho thay canh bao hoac trang thai bi chan.
4. Chuyen sang nhom tu chon de xem tin chi cap nhat.

### Buoc 6: Photobooth

Mo `/photobooth`.

Noi dung thuyet trinh:

Photobooth la tinh nang tao anh ky niem cho sinh vien. Nguoi dung chon frame, upload anh vao tung slot, dieu chinh vi tri, zoom, ap filter, xem preview tren canvas va tai anh PNG ve may.

Gia tri:

- Bien web app hoc thuat thanh mot san pham co tinh su kien va cong dong.
- Frame co the duoc quan ly tu admin, nen phu hop cho cac dip nhu ky niem UIT, ngay hoi khoa, hoac chien dich truyen thong.
- Xu ly anh dien ra tren canvas, nguoi dung thay ket qua truc tiep truoc khi tai ve.

### Buoc 7: Dang nhap sinh vien va bookmark

Mo `/student/login`, sau do `/student/bookmarks`.

Noi dung thuyet trinh:

Sinh vien co tai khoan rieng de dang nhap, dang ky va quan ly bookmark. Sau khi luu mon hoc hoac repository, cac muc nay xuat hien trong trang bookmark ca nhan.

Gia tri:

- Bien viec kham pha thanh qua trinh hoc lap lai.
- Sinh vien co the tao danh sach hoc tap rieng thay vi phai luu link thu cong.

## 4. Demo Flow Admin

### Buoc 8: Admin Dashboard

Mo `/admin/login`, dang nhap, sau do vao `/admin`.

Noi dung thuyet trinh:

DevOrbit khong chi co giao dien sinh vien. He thong co khu vuc admin de quan ly du lieu nguon: mon hoc, repository, candidate tu GitHub, relationship mon hoc, roadmaps, notes va photobooth frames.

Gia tri:

- Du lieu trong web co quy trinh quan tri ro rang.
- Admin co the cap nhat catalogue ma khong can sua code frontend.

### Buoc 9: Quan ly mon hoc va tai nguyen

Mo `/admin/courses`, sau do `/admin/courses/:courseId/resources`.

Noi dung thuyet trinh:

Admin co the them, sua va ngung kich hoat mon hoc. Voi tung mon, admin quan ly them playlist YouTube, article va tutorial. Cac tai nguyen nay quay lai hien thi o trang chi tiet mon hoc cho sinh vien.

Gia tri:

- Mot mon hoc co the duoc lam giau bang nhieu loai tai nguyen.
- Quy trinh quan tri giup catalogue cap nhat theo tung hoc ky.

### Buoc 10: Scan GitHub va duyet repository

Mo `/admin/scan`, sau do `/admin/candidates`.

Noi dung thuyet trinh:

Admin nhap mon hoc va query, backend goi GitHub Search API de lay repository ung vien. Ket qua khong duoc dua thang ra public. No di qua buoc candidate review. Admin xem stars, forks, topics, mo ta, readme excerpt, reviewer note, sau do approve hoac reject.

Gia tri:

- Quy trinh scan -> candidate -> approve giup kiem soat chat luong repo.
- Repository public co nguon goc ro rang, khong phai du lieu nhap tay hoan toan.
- Co the phan cong reviewer de chia viec kiem duyet.

### Buoc 11: Quan ly repo da duyet

Mo `/admin/repos`.

Noi dung thuyet trinh:

Sau khi approve, repository tro thanh tai nguyen chinh thuc. Admin van co the sua ten hien thi, mo ta, URL GitHub, ngon ngu chinh, stars, tech stack, mon hoc gan voi repo va trang thai hoat dong.

Gia tri:

- Du lieu repo co vong doi ro rang.
- Admin co the sua metadata de repo hien thi tot hon cho sinh vien.

### Buoc 12: Quan ly relationship va roadmap

Mo `/admin/relationships` va `/admin/roadmaps`.

Noi dung thuyet trinh:

Relationship giua cac mon hoc la du lieu nen cho knowledge graph. Admin quan ly cac loai quan he nhu prerequisite, complementary va corequisite. Roadmap cho phep tao lo trinh hoc tap gom roadmap, phase va item, trong do item co the tro den course hoac repo.

Gia tri:

- Knowledge graph khong phai hinh anh tinh. No dua tren du lieu quan he duoc quan ly trong backend.
- Roadmap giup dong goi course va repo thanh mot ke hoach hoc co thu tu.

### Buoc 13: Quan ly photobooth frames

Mo `/admin/photobooth-frames`.

Noi dung thuyet trinh:

Admin co the tao frame photobooth, upload overlay, chon so slot anh, chinh vi tri slot va dung auto detect de tim vung trong suot tren PNG. Frame sau do xuat hien cho sinh vien o trang photobooth.

Gia tri:

- Khong can hard-code frame moi.
- Phu hop cho cac su kien can cap nhat khung anh nhanh.

## 5. Backend Ho Tro Nhung Gi

Noi dung thuyet trinh ngan:

Phia sau web la devorbit-api, xay bang Spring Boot. Backend chia thanh controller, service, repository. Controller nhan request REST, service xu ly nghiep vu, repository lam viec voi PostgreSQL.

Mot so nhom API chinh:

- Public API: mon hoc, chi tiet mon, repository, tech stack, discovery feed, knowledge graph.
- AI API: tom tat repo, advice, query knowledge graph, generate roadmap.
- Student API: dang ky, dang nhap, profile, bookmark.
- Admin API: auth, CRUD course, GitHub scan, candidate review, repo management, resources, relationships, roadmaps, notes.
- Photobooth API: frame list, frame detail, tao/xoa frame, upload overlay.

## 6. Diem Nen Nhan Manh Khi Showcase

### 1. DevOrbit co du lieu hoc thuat that

He thong khong chi la UI demo. No co entity, repository, service, controller va PostgreSQL schema de quan ly course, repo, tech stack, roadmap, note, bookmark va photobooth frame.

### 2. Co hai trai nghiem rieng

Sinh vien dung DevOrbit de hoc va kham pha. Admin dung DevOrbit de quan tri va kiem duyet du lieu.

### 3. AI duoc dat vao dung ngu canh

AI khong chi nam o chatbot. AI xuat hien tai noi sinh vien can ra quyet dinh: tom tat repo, goi y cach hoc, query knowledge graph va tao roadmap.

### 4. Knowledge graph la tinh nang noi bat

Day la phan nen demo ky. No noi truc tiep voi bai toan cua sinh vien: hoc mon nao truoc, mon nao phu thuoc mon nao, neu truot mon thi bi anh huong ra sao, va can hoan thanh nhom tu chon nao.

### 5. Photobooth tao diem nho

Sau cac tinh nang hoc thuat, photobooth giup showcase co phan tuong tac, de nguoi xem thay san pham co kha nang phuc vu su kien va cong dong.

## 7. Loi Thoai Ket

Tong ket lai, DevOrbit la mot nen tang web giup sinh vien UIT kham pha mon hoc, tim repository da duoc kiem duyet, hieu chuong trinh dao tao bang knowledge graph, nhan goi y hoc tap bang AI, va tao anh ky niem qua photobooth.

Voi admin, DevOrbit cung cap quy trinh quan tri du lieu day du: tu quan ly mon hoc, scan GitHub, duyet repository, quan ly quan he mon hoc, den cap nhat frame photobooth.

Huong phat trien tiep theo cua nhom la bo sung test cho web, hoan thien them dashboard thong ke nang cao, va mo rong tinh nang danh gia repository de sinh vien co the dong gop phan hoi truc tiep.

## 8. Script 3 Phut

DevOrbit la nen tang giup sinh vien UIT kham pha mon hoc va ma nguon hoc thuat trong mot noi duy nhat.

Trang dau tien la catalogue mon hoc. Sinh vien co the tim mon theo ma hoac ten, xem so tin chi va so repository lien quan. Khi vao chi tiet mon, he thong hien thi repository, tech stack, playlist, article va tutorial neu co.

Voi tung repository, DevOrbit hien thi thong tin can thiet truoc khi mo GitHub: mo ta, ngon ngu, tech stack va stars. Diem khac biet la AI Summary va AI Advice, giup sinh vien hieu nhanh repo nay lam gi va nen hoc theo thu tu nao.

Tinh nang noi bat tiep theo la Knowledge Graph. Sinh vien co the xem lo trinh 8 hoc ky, quan he tien quyet, mon tu chon va cac mon tot nghiep. Khi lap ke hoach, he thong canh bao neu sap xep mon hoc sai dieu kien tien quyet, dong thoi theo doi tin chi.

DevOrbit cung co Photobooth de tao anh ky niem. Nguoi dung chon frame, upload anh, chinh vi tri, ap filter, xem preview va tai anh PNG.

O phia admin, he thong co dashboard rieng. Admin quan ly mon hoc, tai nguyen, relationship, roadmap va photobooth frames. Dac biet, admin co the scan repository tu GitHub, xem danh sach candidate, roi approve hoac reject truoc khi repo xuat hien cho sinh vien.

Nhu vay, DevOrbit khong chi la trang xem mon hoc. No la mot he sinh thai nho gom catalogue, repository, AI, knowledge graph, bookmark, photobooth va quy trinh quan tri du lieu.

## 9. Script 7 Phut

Xin chao thay co va cac ban. Nhom em xin gioi thieu DevOrbit, mot nen tang web danh cho sinh vien UIT de kham pha mon hoc, repository hoc thuat, knowledge graph chuong trinh dao tao va cac tinh nang AI ho tro hoc tap.

Van de DevOrbit giai quyet la: sinh vien thuong phai tim tai lieu o nhieu noi khac nhau. Mon hoc nam trong chuong trinh dao tao, repository nam tren GitHub, lo trinh hoc tap nam trong kinh nghiem ca nhan, con tai lieu hoc lai rai rac o nhieu nguon. DevOrbit gom cac phan do vao mot web app co cau truc.

Dau tien la trang danh sach mon hoc. Sinh vien co the xem catalogue, tim theo ma mon hoac ten mon, va nhin nhanh so repository lien quan. Khi chon mot mon, trang chi tiet hien thi thong tin mon hoc, repo lien quan, tech stack, video, bai viet va tutorial. Neu sinh vien quan tam, co the bookmark mon hoc de xem lai sau.

Tiep theo la trang repository. Thay vi chi dua link GitHub, DevOrbit hien thi metadata cua repo va bo sung AI Summary, AI Advice. AI Summary giup hieu nhanh repo lam gi. AI Advice dua ra chien luoc hoc tap, vi du nen bat dau tu README, module nao dang quan trong, hoac repo phu hop voi muc tieu nao.

Phan quan trong nhat la Knowledge Graph. DevOrbit truc quan hoa chuong trinh dao tao theo 8 hoc ky, gom mon bat buoc, mon tu chon, dieu kien tien quyet va cac mon tot nghiep. Sinh vien co the sap xep ke hoach hoc, xem canh bao khi thieu tien quyet, va theo doi tin chi theo nhom. Day la phan giup sinh vien bien chuong trinh dao tao thanh mot ban do hoc tap co the tuong tac.

Sau do la Photobooth. Day la tinh nang tao anh ky niem. Nguoi dung chon frame, upload anh vao slot, chinh vi tri, zoom, ap filter, xem preview tren canvas va tai anh ve. Tinh nang nay giup DevOrbit khong chi phuc vu hoc tap, ma con co the dung cho su kien va cong dong UIT.

O phia admin, DevOrbit co mot khu vuc quan tri rieng. Admin dang nhap bang JWT, quan ly mon hoc, tai nguyen, repository, relationship, roadmap, note va frame photobooth.

Quy trinh quan trong nhat cua admin la GitHub scan. Admin chon mon hoc va query, backend goi GitHub Search API de lay repo ung vien. Cac repo nay chua hien thi public ngay. Chung di qua man hinh candidate review, noi admin xem stars, forks, topics, mo ta, readme excerpt, reviewer note, sau do approve hoac reject. Khi approve, repo moi tro thanh tai nguyen chinh thuc trong trang sinh vien.

Ve ky thuat, web duoc xay bang React 19, TypeScript, Vite, Tailwind, React Router va TanStack Query. Backend la Spring Boot voi cau truc controller, service, repository va PostgreSQL. Cac API chia thanh public, student, admin, AI va photobooth.

Tong ket, DevOrbit tap trung vao ba gia tri: giup sinh vien tim dung tai nguyen nhanh hon, giup sinh vien hieu lo trinh hoc ro hon, va giup admin quan ly du lieu hoc thuat co kiem duyet. Huong tiep theo la bo sung test UI, mo rong dashboard thong ke va them tinh nang danh gia repository.

## 10. Slide Outline Goi Y

1. Problem: Sinh vien tim tai lieu, repo va lo trinh hoc bi phan tan.
2. Solution: DevOrbit gom course catalogue, repo, knowledge graph, AI va photobooth.
3. Student Flow: Courses -> Course Detail -> Repo Detail -> Bookmark.
4. AI Flow: Summary, Advice, Knowledge Graph Query, Roadmap Generator.
5. Knowledge Graph: 8 hoc ky, tien quyet, tu chon, tin chi, tot nghiep.
6. Photobooth: Frame, upload, filter, preview, download.
7. Admin Flow: Course CRUD, GitHub scan, candidate review, approved repos.
8. Data Governance: Scan -> Review -> Approve -> Public.
9. Tech Stack: React web, Spring Boot API, PostgreSQL, Docker Compose.
10. Closing: DevOrbit la nen tang hoc tap va quan tri ma nguon hoc thuat cho UIT.

