import { useState } from "react";
import { Collapse } from "@mui/material";
import { activityService } from "../services/activityService";

const ParticipantsList = ({ activityId, onError }) => {
  const [participants, setParticipants] = useState(null);
  const [isExpanded, setIsExpanded] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const fetchParticipants = async () => {
    if (participants) return; // Already loaded
    
    setIsLoading(true);
    try {
      const response = await activityService.getActivityParticipants(activityId);
      setParticipants(response.content || []);
    } catch (error) {
      console.error("Failed to fetch participants:", error);
      onError?.("Failed to load participants");
    } finally {
      setIsLoading(false);
    }
  };

  const toggleExpand = async () => {
    const wasExpanded = isExpanded;
    setIsExpanded(!wasExpanded);
    
    if (!wasExpanded && !participants) {
      await fetchParticipants();
    }
  };

  return (
    <>
      <button style={styles.participantsButton} onClick={toggleExpand}>
        {isExpanded ? "Hide Participants" : "Show Participants"}
      </button>
      <Collapse in={isExpanded}>
        <div style={styles.participantsSection}>
          <h4 style={styles.participantsTitle}>Participants:</h4>
          {isLoading ? (
            <div style={styles.loadingText}>Loading participants...</div>
          ) : participants && participants.length > 0 ? (
            <div style={styles.participantsList}>
              {participants.map((participant, index) => (
                <div key={index} style={styles.participantItem}>
                  <div style={styles.participantAvatar}>
                    {participant.username.charAt(0).toUpperCase()}
                  </div>
                  <div style={styles.participantInfo}>
                    <span style={styles.participantName}>{participant.username}</span>
                    {participant.roleType === "ADMIN" && (
                      <span style={styles.adminBadge}>Admin</span>
                    )}
                  </div>
                </div>
              ))}
            </div>
          ) : participants && participants.length === 0 ? (
            <div style={styles.loadingText}>No participants found</div>
          ) : null}
        </div>
      </Collapse>
    </>
  );
};

const styles = {
  participantsButton: {
    backgroundColor: "#6b7280",
    color: "white",
    border: "none",
    padding: "0.75rem 1rem",
    borderRadius: "0.5rem",
    fontSize: "0.85rem",
    fontWeight: "600",
    cursor: "pointer",
    transition: "background-color 0.2s",
    flex: "1",
    whiteSpace: "nowrap",
  },
  participantsSection: {
    marginTop: "1rem",
    padding: "1rem",
    backgroundColor: "#f9fafb",
    borderRadius: "0.5rem",
    border: "1px solid #e5e7eb",
  },
  participantsTitle: {
    margin: "0 0 0.75rem 0",
    color: "#374151",
    fontSize: "1rem",
    fontWeight: "600",
  },
  participantsList: {
    display: "flex",
    flexDirection: "column",
    gap: "0.5rem",
  },
  participantItem: {
    display: "flex",
    alignItems: "center",
    gap: "0.75rem",
    padding: "0.5rem",
    backgroundColor: "white",
    borderRadius: "0.375rem",
    border: "1px solid #e5e7eb",
  },
  participantAvatar: {
    width: "32px",
    height: "32px",
    backgroundColor: "#3b82f6",
    color: "white",
    borderRadius: "50%",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontWeight: "bold",
    fontSize: "0.875rem",
  },
  participantInfo: {
    display: "flex",
    alignItems: "center",
    gap: "0.5rem",
    flex: "1",
  },
  participantName: {
    color: "#1f2937",
    fontSize: "0.9rem",
    fontWeight: "500",
  },
  adminBadge: {
    backgroundColor: "#fbbf24",
    color: "#92400e",
    padding: "0.125rem 0.5rem",
    borderRadius: "0.25rem",
    fontSize: "0.75rem",
    fontWeight: "600",
  },
  loadingText: {
    color: "#6b7280",
    fontSize: "0.875rem",
    fontStyle: "italic",
    textAlign: "center",
    padding: "1rem",
  },
};

export default ParticipantsList; 