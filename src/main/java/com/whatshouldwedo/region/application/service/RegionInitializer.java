package com.whatshouldwedo.region.application.service;

import com.whatshouldwedo.region.application.port.out.SidoRepository;
import com.whatshouldwedo.region.application.port.out.SigunguRepository;
import com.whatshouldwedo.region.domain.Sido;
import com.whatshouldwedo.region.domain.SidoId;
import com.whatshouldwedo.region.domain.Sigungu;
import com.whatshouldwedo.region.domain.SigunguId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class RegionInitializer implements ApplicationRunner {

    private final SidoRepository sidoRepository;
    private final SigunguRepository sigunguRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("전국 행정구역 초기화 시작...");
        initSidoAndSigungu();
        log.info("전국 행정구역 초기화 완료 - 시도 {}개, 시군구 {}개",
                sidoRepository.findAll().size(), "전체");
    }

    private void initSidoAndSigungu() {
        // ========== 서울특별시 ==========
        SidoId seoul = seedSido("11", "서울특별시");
        seedSigungu(seoul, "11010", "종로구");
        seedSigungu(seoul, "11020", "중구");
        seedSigungu(seoul, "11030", "용산구");
        seedSigungu(seoul, "11040", "성동구");
        seedSigungu(seoul, "11050", "광진구");
        seedSigungu(seoul, "11060", "동대문구");
        seedSigungu(seoul, "11070", "중랑구");
        seedSigungu(seoul, "11080", "성북구");
        seedSigungu(seoul, "11090", "강북구");
        seedSigungu(seoul, "11100", "도봉구");
        seedSigungu(seoul, "11110", "노원구");
        seedSigungu(seoul, "11120", "은평구");
        seedSigungu(seoul, "11130", "서대문구");
        seedSigungu(seoul, "11140", "마포구");
        seedSigungu(seoul, "11150", "양천구");
        seedSigungu(seoul, "11160", "강서구");
        seedSigungu(seoul, "11170", "구로구");
        seedSigungu(seoul, "11180", "금천구");
        seedSigungu(seoul, "11190", "영등포구");
        seedSigungu(seoul, "11200", "동작구");
        seedSigungu(seoul, "11210", "관악구");
        seedSigungu(seoul, "11220", "서초구");
        seedSigungu(seoul, "11230", "강남구");
        seedSigungu(seoul, "11240", "송파구");
        seedSigungu(seoul, "11250", "강동구");

        // ========== 부산광역시 ==========
        SidoId busan = seedSido("26", "부산광역시");
        seedSigungu(busan, "26010", "중구");
        seedSigungu(busan, "26020", "서구");
        seedSigungu(busan, "26030", "동구");
        seedSigungu(busan, "26040", "영도구");
        seedSigungu(busan, "26050", "부산진구");
        seedSigungu(busan, "26060", "동래구");
        seedSigungu(busan, "26070", "남구");
        seedSigungu(busan, "26080", "북구");
        seedSigungu(busan, "26090", "해운대구");
        seedSigungu(busan, "26100", "사하구");
        seedSigungu(busan, "26110", "금정구");
        seedSigungu(busan, "26120", "강서구");
        seedSigungu(busan, "26130", "연제구");
        seedSigungu(busan, "26140", "수영구");
        seedSigungu(busan, "26150", "사상구");
        seedSigungu(busan, "26310", "기장군");

        // ========== 대구광역시 ==========
        SidoId daegu = seedSido("27", "대구광역시");
        seedSigungu(daegu, "27010", "중구");
        seedSigungu(daegu, "27020", "동구");
        seedSigungu(daegu, "27030", "서구");
        seedSigungu(daegu, "27040", "남구");
        seedSigungu(daegu, "27050", "북구");
        seedSigungu(daegu, "27060", "수성구");
        seedSigungu(daegu, "27070", "달서구");
        seedSigungu(daegu, "27310", "달성군");
        seedSigungu(daegu, "27320", "군위군");

        // ========== 인천광역시 ==========
        SidoId incheon = seedSido("28", "인천광역시");
        seedSigungu(incheon, "28010", "중구");
        seedSigungu(incheon, "28020", "동구");
        seedSigungu(incheon, "28030", "미추홀구");
        seedSigungu(incheon, "28040", "연수구");
        seedSigungu(incheon, "28050", "남동구");
        seedSigungu(incheon, "28060", "부평구");
        seedSigungu(incheon, "28070", "계양구");
        seedSigungu(incheon, "28080", "서구");
        seedSigungu(incheon, "28310", "강화군");
        seedSigungu(incheon, "28320", "옹진군");

        // ========== 광주광역시 ==========
        SidoId gwangju = seedSido("29", "광주광역시");
        seedSigungu(gwangju, "29010", "동구");
        seedSigungu(gwangju, "29020", "서구");
        seedSigungu(gwangju, "29030", "남구");
        seedSigungu(gwangju, "29040", "북구");
        seedSigungu(gwangju, "29050", "광산구");

        // ========== 대전광역시 ==========
        SidoId daejeon = seedSido("30", "대전광역시");
        seedSigungu(daejeon, "30010", "동구");
        seedSigungu(daejeon, "30020", "중구");
        seedSigungu(daejeon, "30030", "서구");
        seedSigungu(daejeon, "30040", "유성구");
        seedSigungu(daejeon, "30050", "대덕구");

        // ========== 울산광역시 ==========
        SidoId ulsan = seedSido("31", "울산광역시");
        seedSigungu(ulsan, "31010", "중구");
        seedSigungu(ulsan, "31020", "남구");
        seedSigungu(ulsan, "31030", "동구");
        seedSigungu(ulsan, "31040", "북구");
        seedSigungu(ulsan, "31310", "울주군");

        // ========== 세종특별자치시 ==========
        SidoId sejong = seedSido("36", "세종특별자치시");
        seedSigungu(sejong, "36010", "세종시");

        // ========== 경기도 ==========
        SidoId gyeonggi = seedSido("41", "경기도");
        seedSigungu(gyeonggi, "41010", "수원시");
        seedSigungu(gyeonggi, "41020", "성남시");
        seedSigungu(gyeonggi, "41030", "의정부시");
        seedSigungu(gyeonggi, "41040", "안양시");
        seedSigungu(gyeonggi, "41050", "부천시");
        seedSigungu(gyeonggi, "41060", "광명시");
        seedSigungu(gyeonggi, "41070", "평택시");
        seedSigungu(gyeonggi, "41080", "동두천시");
        seedSigungu(gyeonggi, "41090", "안산시");
        seedSigungu(gyeonggi, "41100", "고양시");
        seedSigungu(gyeonggi, "41110", "과천시");
        seedSigungu(gyeonggi, "41120", "구리시");
        seedSigungu(gyeonggi, "41130", "남양주시");
        seedSigungu(gyeonggi, "41140", "오산시");
        seedSigungu(gyeonggi, "41150", "시흥시");
        seedSigungu(gyeonggi, "41160", "군포시");
        seedSigungu(gyeonggi, "41170", "의왕시");
        seedSigungu(gyeonggi, "41180", "하남시");
        seedSigungu(gyeonggi, "41190", "용인시");
        seedSigungu(gyeonggi, "41200", "파주시");
        seedSigungu(gyeonggi, "41210", "이천시");
        seedSigungu(gyeonggi, "41220", "안성시");
        seedSigungu(gyeonggi, "41230", "김포시");
        seedSigungu(gyeonggi, "41240", "화성시");
        seedSigungu(gyeonggi, "41250", "광주시");
        seedSigungu(gyeonggi, "41260", "양주시");
        seedSigungu(gyeonggi, "41270", "포천시");
        seedSigungu(gyeonggi, "41280", "여주시");
        seedSigungu(gyeonggi, "41310", "연천군");
        seedSigungu(gyeonggi, "41320", "가평군");
        seedSigungu(gyeonggi, "41330", "양평군");

        // ========== 강원특별자치도 ==========
        SidoId gangwon = seedSido("51", "강원특별자치도");
        seedSigungu(gangwon, "51010", "춘천시");
        seedSigungu(gangwon, "51020", "원주시");
        seedSigungu(gangwon, "51030", "강릉시");
        seedSigungu(gangwon, "51040", "동해시");
        seedSigungu(gangwon, "51050", "태백시");
        seedSigungu(gangwon, "51060", "속초시");
        seedSigungu(gangwon, "51070", "삼척시");
        seedSigungu(gangwon, "51310", "홍천군");
        seedSigungu(gangwon, "51320", "횡성군");
        seedSigungu(gangwon, "51330", "영월군");
        seedSigungu(gangwon, "51340", "평창군");
        seedSigungu(gangwon, "51350", "정선군");
        seedSigungu(gangwon, "51360", "철원군");
        seedSigungu(gangwon, "51370", "화천군");
        seedSigungu(gangwon, "51380", "양구군");
        seedSigungu(gangwon, "51390", "인제군");
        seedSigungu(gangwon, "51400", "고성군");
        seedSigungu(gangwon, "51410", "양양군");

        // ========== 충청북도 ==========
        SidoId chungbuk = seedSido("43", "충청북도");
        seedSigungu(chungbuk, "43010", "청주시");
        seedSigungu(chungbuk, "43020", "충주시");
        seedSigungu(chungbuk, "43030", "제천시");
        seedSigungu(chungbuk, "43310", "보은군");
        seedSigungu(chungbuk, "43320", "옥천군");
        seedSigungu(chungbuk, "43330", "영동군");
        seedSigungu(chungbuk, "43340", "증평군");
        seedSigungu(chungbuk, "43350", "진천군");
        seedSigungu(chungbuk, "43360", "괴산군");
        seedSigungu(chungbuk, "43370", "음성군");
        seedSigungu(chungbuk, "43380", "단양군");

        // ========== 충청남도 ==========
        SidoId chungnam = seedSido("44", "충청남도");
        seedSigungu(chungnam, "44010", "천안시");
        seedSigungu(chungnam, "44020", "공주시");
        seedSigungu(chungnam, "44030", "보령시");
        seedSigungu(chungnam, "44040", "아산시");
        seedSigungu(chungnam, "44050", "서산시");
        seedSigungu(chungnam, "44060", "논산시");
        seedSigungu(chungnam, "44070", "계룡시");
        seedSigungu(chungnam, "44080", "당진시");
        seedSigungu(chungnam, "44310", "금산군");
        seedSigungu(chungnam, "44320", "부여군");
        seedSigungu(chungnam, "44330", "서천군");
        seedSigungu(chungnam, "44340", "청양군");
        seedSigungu(chungnam, "44350", "홍성군");
        seedSigungu(chungnam, "44360", "예산군");
        seedSigungu(chungnam, "44370", "태안군");

        // ========== 전북특별자치도 ==========
        SidoId jeonbuk = seedSido("45", "전북특별자치도");
        seedSigungu(jeonbuk, "45010", "전주시");
        seedSigungu(jeonbuk, "45020", "군산시");
        seedSigungu(jeonbuk, "45030", "익산시");
        seedSigungu(jeonbuk, "45040", "정읍시");
        seedSigungu(jeonbuk, "45050", "남원시");
        seedSigungu(jeonbuk, "45060", "김제시");
        seedSigungu(jeonbuk, "45310", "완주군");
        seedSigungu(jeonbuk, "45320", "진안군");
        seedSigungu(jeonbuk, "45330", "무주군");
        seedSigungu(jeonbuk, "45340", "장수군");
        seedSigungu(jeonbuk, "45350", "임실군");
        seedSigungu(jeonbuk, "45360", "순창군");
        seedSigungu(jeonbuk, "45370", "고창군");
        seedSigungu(jeonbuk, "45380", "부안군");

        // ========== 전라남도 ==========
        SidoId jeonnam = seedSido("46", "전라남도");
        seedSigungu(jeonnam, "46010", "목포시");
        seedSigungu(jeonnam, "46020", "여수시");
        seedSigungu(jeonnam, "46030", "순천시");
        seedSigungu(jeonnam, "46040", "나주시");
        seedSigungu(jeonnam, "46050", "광양시");
        seedSigungu(jeonnam, "46310", "담양군");
        seedSigungu(jeonnam, "46320", "곡성군");
        seedSigungu(jeonnam, "46330", "구례군");
        seedSigungu(jeonnam, "46340", "고흥군");
        seedSigungu(jeonnam, "46350", "보성군");
        seedSigungu(jeonnam, "46360", "화순군");
        seedSigungu(jeonnam, "46370", "장흥군");
        seedSigungu(jeonnam, "46380", "강진군");
        seedSigungu(jeonnam, "46390", "해남군");
        seedSigungu(jeonnam, "46400", "영암군");
        seedSigungu(jeonnam, "46410", "무안군");
        seedSigungu(jeonnam, "46420", "함평군");
        seedSigungu(jeonnam, "46430", "영광군");
        seedSigungu(jeonnam, "46440", "장성군");
        seedSigungu(jeonnam, "46450", "완도군");
        seedSigungu(jeonnam, "46460", "진도군");
        seedSigungu(jeonnam, "46470", "신안군");

        // ========== 경상북도 ==========
        SidoId gyeongbuk = seedSido("47", "경상북도");
        seedSigungu(gyeongbuk, "47010", "포항시");
        seedSigungu(gyeongbuk, "47020", "경주시");
        seedSigungu(gyeongbuk, "47030", "김천시");
        seedSigungu(gyeongbuk, "47040", "안동시");
        seedSigungu(gyeongbuk, "47050", "구미시");
        seedSigungu(gyeongbuk, "47060", "영주시");
        seedSigungu(gyeongbuk, "47070", "영천시");
        seedSigungu(gyeongbuk, "47080", "상주시");
        seedSigungu(gyeongbuk, "47090", "문경시");
        seedSigungu(gyeongbuk, "47100", "경산시");
        seedSigungu(gyeongbuk, "47310", "의성군");
        seedSigungu(gyeongbuk, "47320", "청송군");
        seedSigungu(gyeongbuk, "47330", "영양군");
        seedSigungu(gyeongbuk, "47340", "영덕군");
        seedSigungu(gyeongbuk, "47350", "청도군");
        seedSigungu(gyeongbuk, "47360", "고령군");
        seedSigungu(gyeongbuk, "47370", "성주군");
        seedSigungu(gyeongbuk, "47380", "칠곡군");
        seedSigungu(gyeongbuk, "47390", "예천군");
        seedSigungu(gyeongbuk, "47400", "봉화군");
        seedSigungu(gyeongbuk, "47410", "울진군");
        seedSigungu(gyeongbuk, "47420", "울릉군");

        // ========== 경상남도 ==========
        SidoId gyeongnam = seedSido("48", "경상남도");
        seedSigungu(gyeongnam, "48010", "창원시");
        seedSigungu(gyeongnam, "48020", "진주시");
        seedSigungu(gyeongnam, "48030", "통영시");
        seedSigungu(gyeongnam, "48040", "사천시");
        seedSigungu(gyeongnam, "48050", "김해시");
        seedSigungu(gyeongnam, "48060", "밀양시");
        seedSigungu(gyeongnam, "48070", "거제시");
        seedSigungu(gyeongnam, "48080", "양산시");
        seedSigungu(gyeongnam, "48310", "의령군");
        seedSigungu(gyeongnam, "48320", "함안군");
        seedSigungu(gyeongnam, "48330", "창녕군");
        seedSigungu(gyeongnam, "48340", "고성군");
        seedSigungu(gyeongnam, "48350", "남해군");
        seedSigungu(gyeongnam, "48360", "하동군");
        seedSigungu(gyeongnam, "48370", "산청군");
        seedSigungu(gyeongnam, "48380", "함양군");
        seedSigungu(gyeongnam, "48390", "거창군");
        seedSigungu(gyeongnam, "48400", "합천군");

        // ========== 제주특별자치도 ==========
        SidoId jeju = seedSido("50", "제주특별자치도");
        seedSigungu(jeju, "50010", "제주시");
        seedSigungu(jeju, "50020", "서귀포시");
    }

    private SidoId seedSido(String code, String name) {
        if (sidoRepository.existsByCode(code)) {
            return sidoRepository.findByCode(code).orElseThrow().getId();
        }
        Sido sido = Sido.create(SidoId.generate(), code, name);
        return sidoRepository.save(sido).getId();
    }

    private void seedSigungu(SidoId sidoId, String code, String name) {
        if (sigunguRepository.existsByCode(code)) {
            return;
        }
        Sigungu sigungu = Sigungu.create(SigunguId.generate(), sidoId, code, name);
        sigunguRepository.save(sigungu);
    }
}
