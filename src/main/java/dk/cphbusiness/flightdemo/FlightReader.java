package dk.cphbusiness.flightdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.cphbusiness.flightdemo.dtos.FlightDTO;
import dk.cphbusiness.flightdemo.dtos.FlightInfoDTO;
import dk.cphbusiness.utils.Utils;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */
public class FlightReader {

    public static void main(String[] args) {
        try {
            List<FlightDTO> flightList = getFlightsFromFile("flights.json");
            List<FlightInfoDTO> flightInfoDTOList = getFlightInfoDetails(flightList);


            long totalMinutes = flightInfoDTOList.stream()
                    .filter(f -> "Aeroflot".equals(f.getAirline()))
                    .map(FlightInfoDTO::getDuration)
                    .mapToLong(Duration::toMinutes)
                    .sum();

            System.out.println("Total minutes: " + totalMinutes);

            System.out.println();


            //Task 1
            double averageTime = flightInfoDTOList.stream()
                    .filter(f -> "Aeroflot".equals(f.getAirline()))
                    .map(FlightInfoDTO::getDuration)
                    .mapToLong(Duration::toMinutes)
                    .average()
                    .orElse(0); // in case there are no flights

            System.out.println("Average time: " + averageTime);

            System.out.println();



            //Task 2
            List<FlightInfoDTO> flightsBothWays = flightInfoDTOList.stream()
                    .filter(f -> ("Fukuoka".equals(f.getOrigin()) && "Haneda Airport".equals(f.getDestination())) ||
                            ("Haneda Airport".equals(f.getOrigin()) && "Fukuoka".equals(f.getDestination())))
                    .distinct()
                    .toList();



            flightsBothWays.forEach(f -> {
                System.out.println("Flight Number: " + f.getName());
                System.out.println("IATA: " + f.getIata());
                System.out.println("Airline: " + f.getAirline());
                System.out.println("Origin: " + f.getOrigin());
                System.out.println("Destination: " + f.getDestination());
                System.out.println("Departure: " + f.getDeparture());
                System.out.println("Arrival: " + f.getArrival());
                System.out.println("Duration: " + f.getDuration());
                System.out.println("-----------------------------");
            });






//            flightInfoDTOList.forEach(System.out::println);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<FlightDTO> getFlightsFromFile(String filename) throws IOException {

        ObjectMapper objectMapper = Utils.getObjectMapper();

        // Deserialize JSON from a file into FlightDTO[]
        FlightDTO[] flightsArray = objectMapper.readValue(Paths.get("flights.json").toFile(), FlightDTO[].class);

        // Convert to a list
        List<FlightDTO> flightsList = List.of(flightsArray);
        return flightsList;
    }


        public static List<FlightInfoDTO> getFlightInfoDetails(List<FlightDTO> flightList) {
            List<FlightInfoDTO> flightInfoList = flightList.stream()
               .map(flight -> {
                    LocalDateTime departure = flight.getDeparture().getScheduled();
                    LocalDateTime arrival = flight.getArrival().getScheduled();
                    Duration duration = Duration.between(departure, arrival);
                    FlightInfoDTO flightInfo =
                            FlightInfoDTO.builder()
                                .name(flight.getFlight().getNumber())
                                .iata(flight.getFlight().getIata())
                                .airline(flight.getAirline().getName())
                                .duration(duration)
                                .departure(departure)
                                .arrival(arrival)
                                .origin(flight.getDeparture().getAirport())
                                .destination(flight.getArrival().getAirport())
                                .build();

                    return flightInfo;
                })
            .toList();
            return flightInfoList;
        }

}
