package u17112631.helpers;

import u17112631.dto.constraints.InstitutionalWeighting;
import u17112631.dto.constraints.hardConstraints.period.PeriodHardConstraint;
import u17112631.dto.constraints.hardConstraints.room.RoomHardConstraint;
import u17112631.dto.primitives.Exam;
import u17112631.dto.primitives.ExamProblemSet;
import u17112631.dto.primitives.Period;
import u17112631.dto.primitives.Room;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ExamFileReader {

    private final String _fileName;

    public ExamFileReader(String name){
        _fileName = name;
    }

    public ExamProblemSet CreateProblemSetFromFile() throws Exception {

        File f = new File(_fileName);
        FileInputStream f1 = new FileInputStream(f);
        BufferedReader df = new BufferedReader(new InputStreamReader(f1));

        try {
            //Read exams
            int numExams = GetDeclarationAmount(df.readLine(), "Exams");
            List<Exam> exams = ReadExams(df,numExams);

            //Read periods
            int numPeriods = GetDeclarationAmount(df.readLine(),"Periods");
            List<Period> periods = ReadPeriods(df,numPeriods);

            //Read rooms
            int numRooms = GetDeclarationAmount(df.readLine(),"Rooms");
            List<Room> rooms = ReadRooms(df,numRooms);

            for (Period period: periods){
                List<Room> roomCopies = new ArrayList<>();

                for (Room room :
                        rooms) {
                    roomCopies.add(room.getCopy());
                }
                period.setRooms(roomCopies);
            }
            //Constrains
            List<PeriodHardConstraint> periodConstraints = ReadPeriodConstraints(df);
            List<RoomHardConstraint> roomConstraints = ReadRoomConstraints(df);
            List<InstitutionalWeighting> institutionalWeightings = ReadInstitutionalWeightings(df);

            ExamProblemSet problemSet = new ExamProblemSet();
            problemSet.setExams(exams);
            problemSet.setPeriods(periods);
            problemSet.setRooms(rooms);
            problemSet.setPeriodHardConstraints(periodConstraints);
            problemSet.set_roomHardConstraints(roomConstraints);
            problemSet.setSoftConstraints(institutionalWeightings);

            return problemSet;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
            throw new Exception("Could not initialise problem set : "+ _fileName);
        }

    }

    private List<InstitutionalWeighting> ReadInstitutionalWeightings(BufferedReader df) throws IOException {

        List<InstitutionalWeighting> weightings = new ArrayList<>();

        String input = df.readLine();

        while(input != null && !input.isBlank()) {
            weightings.add( new InstitutionalWeighting(input));
            input = df.readLine();
        }

        return weightings;
    }

    //Call after PeriodConstraints
    private List<RoomHardConstraint> ReadRoomConstraints(BufferedReader df) throws IOException {

        List<RoomHardConstraint> roomHardConstraints = new ArrayList<>();

        String input = df.readLine();

        while(!input.contains("[")) {
            roomHardConstraints.add( new RoomHardConstraint(input));
            input = df.readLine();
        }

        return roomHardConstraints;
    }

    private List<PeriodHardConstraint> ReadPeriodConstraints(BufferedReader df) throws IOException {

        List<PeriodHardConstraint> periodHardConstraints = new ArrayList<>();

        df.readLine();
        String input = df.readLine();

        while(!input.contains("[")) {
            periodHardConstraints.add( new PeriodHardConstraint(input));
            input = df.readLine();
        }

        return periodHardConstraints;
    }

    private List<Room> ReadRooms(BufferedReader df, int numRooms) throws IOException {
        
        List<Room> rooms = new ArrayList<>();

        for (int i = 0; i < numRooms; i++) {
            String room = df.readLine();
            rooms.add( new Room(room,i));
        }
        return rooms;
    }

    private List<Exam> ReadExams(BufferedReader df, int numExams) throws IOException {

        List<Exam> exams = new ArrayList<>();

        for (int i = 0; i < numExams; i++) {
            String exam = df.readLine();
            exams.add( new Exam(exam,i));
        }
        return exams;
    }

    private List<Period> ReadPeriods(BufferedReader df, int numPeriods) throws IOException, ParseException {

        List<Period> periods = new ArrayList<>();

        for (int i = 0; i < numPeriods; i++) {
            String period = df.readLine();
            periods.add( new Period(period,i));
        }
        return periods;
    }

    private int GetDeclarationAmount(String sourceLine,String declarationName){

        String declaration = sourceLine.replace("[","");
        declaration = declaration.replace("]","");

        declaration = declaration.replace(declarationName + ":","");

        return Integer.parseInt(declaration);
    }
}
