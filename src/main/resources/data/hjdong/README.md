# 행정동 마스터 데이터

## 데이터 소스
행정안전부 행정표준코드관리시스템 (https://code.go.kr)

## CSV 형식
```
code,name,sigunguCode
1101053,청운효자동,11010
1101054,사직동,11010
...
```

- code: 행정동코드 7자리 (KOSIS C1 기준)
- name: 행정동명
- sigunguCode: 소속 시군구코드 5자리

## 파일 목록
- hjdong_2021.csv ~ hjdong_2026.csv: 연도별 행정동 목록
- mapping_2021_2022.csv ~ mapping_2025_2026.csv: 연도간 행정동 변경 매핑

## 매핑 CSV 형식
```
sourceCode,targetCode,mappingType,ratio,description
1101053,1101053,UNCHANGED,1.0,
```

## 법정동-행정동 매핑
- resources/data/bjdong_hjdong_mapping.csv
- 형식: bjdongCode,hjdongCode
