 package bookings.domain;

 import com.google.protobuf.Empty;
 import io.cloudstate.javasupport.EntityId;
 import io.cloudstate.javasupport.eventsourced.*;
 import io.cloudstate.javasupport.eventsourced.EventSourcedEntity;

 import bookings.*;
 import bookings.domain.*;

 /**
  * A flight reservation domain entity.
  */
 @EventSourcedEntity
 public class FlightReservationEntity {

     private String reservationId;

     /**
      * The flight number being reserved.
      */
     private String flightNumber;

     /**
      * This reservation has received the reserve command and is in the reserved state.
      */
     private Boolean reserved;

     /**
      * This reservation has received the cancel command and is in the cancellation state.
      */
     private Boolean cancelled;

     /**
      * Constructor.
      * @param reservationId The entity id will be the same as this.
      */
     public FlightReservationEntity(@EntityId String reservationId) {
         this.reservationId = reservationId;
     }

     /**
      * Put this entity in the reserved state and emit event.
      */
     @CommandHandler
     public Empty reserveFlightHandler(Bookings.ReserveFlightCommand cmd, CommandContext ctx) {
         if (reserved) {
             ctx.fail("Flight already reserved");
         } else if (cancelled) {
             ctx.fail("Cancelled flight cannot be reserved again.");
         }

         ctx.emit(Domain.FlightReserved.newBuilder()
                 .setReservationId(cmd.getReservationId())
                 .setFlightNumber(cmd.getFlightNumber()).build());

         return Empty.getDefaultInstance();
     }

     /**
      * Handle reserved event previously emitted.
      */
     @EventHandler
     public void flightReservedHandler(Domain.FlightReserved flightReserved) {
         reserved = true;
         flightNumber = flightReserved.getFlightNumber();
     }

     /**
      * Put this entity in the cancelled state and emit event.
      */
     @CommandHandler
     public Empty cancelFlightHandler(Bookings.CancelFlightReservationCommand cmd, CommandContext ctx) {
         if (!reserved) {
             ctx.fail("Flight must be reserved before it can be cancelled.");
         } else if (cancelled) {
             ctx.fail("Cancelled flight cannot be cancelled again.");
         }
         ctx.emit(Domain.FlightCancelled.newBuilder()
                 .setReservationId(cmd.getReservationId()).build());

         return Empty.getDefaultInstance();
     }

     /**
      * Handle cancelled event previously emitted.
      */
     @EventHandler
     public void flightCancelledHandler(Domain.FlightCancelled flightCancelled) {
         cancelled = true;
     }
 }