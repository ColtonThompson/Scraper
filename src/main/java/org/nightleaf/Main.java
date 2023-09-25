package org.nightleaf;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {

    public static String[] CSV_HEADERS = { "Name", "Role", "Join Date" };
    public static final String CSS_MEMBERCARD_CLASS = ".ipsMemberCard";
    public static final String CSS_MEMBERCARD_NAME_CLASS = ".ipsMemberCard_name";
    public static final String CSS_MEMBERCARD_ROLE_CLASS = ".cClubMemberStatus";
    public static final String CSS_MEMBER_JOIN_DATE_CLASS = ".ipsType_reset";

    public static List<Member> memberList = new ArrayList<Member>();

    public static void main(String[] args) {
        for (int page = 0; page <= 4; page++) {
            scrapeSite("https://forum.fishingplanet.com/index.php?/clubs/1-pro-hookers/&do=members&page=" + page);
        }
        dumpToCSV();
    }

    public static void scrapeSite(String url) {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            int responseCode = con.getResponseCode();
            //System.out.println("Response code: " + responseCode);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            String html = response.toString();
            Document doc = Jsoup.parse(html);

            for (Element member : doc.select(CSS_MEMBERCARD_CLASS)) {
                Element memberName = member.select(CSS_MEMBERCARD_NAME_CLASS).first();
                Element roleName = member.select(CSS_MEMBERCARD_ROLE_CLASS).first();
                Element joinDate = member.select(CSS_MEMBER_JOIN_DATE_CLASS).first();
                Member m = new Member(memberName.text(), roleName == null ? "Member" : roleName.text(), joinDate.text());
                addMember(m);
            }
            System.out.println("[SCRAPER] Memberlist is now at " + memberList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dumpToCSV() {
        Writer writer = null;
        try {
            writer = Files.newBufferedWriter(Paths.get("memberlist.csv"));
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader(CSV_HEADERS)
                    .setSkipHeaderRecord(false)
                    .build();
            CSVPrinter csvPrinter = new CSVPrinter(writer, format);

            for (int i = 0; i < memberList.size(); i++) {
                Member member = memberList.get(i);
                csvPrinter.printRecord(member.getName(), member.getRole(), member.getJoinDate());
            }
            System.out.println("[DUMP] Dumped " + memberList.size() + " members to memberlist.csv");
            writer.flush();
            writer.close();
            csvPrinter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean isMemberOnList(Member m) {
        for (Member member : memberList) {
            String name = member.getName();
            if (name.equals(m.getName())) {
                return true;
            }
        }
        return false;
    }

    public static void addMember(Member m) {
        if (isMemberOnList(m)) {
            //System.out.println("[WARNING] " + m.getName() + " was already in the member list, not adding to list!");
            return;
        }
        memberList.add(m);

    }
}