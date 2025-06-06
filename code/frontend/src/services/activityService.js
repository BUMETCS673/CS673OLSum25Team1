<<<<<<< HEAD
import api from "./api";

export const activityService = {
  async getActivities(search = "", sort = "") {
    const params = new URLSearchParams();
    if (search) params.append("search", search);
    if (sort) params.append("sort", sort);

    const response = await api.get(`/activities?${params.toString()}`);
    return response.data;
  },
};
=======
import api from "./api";

export const activityService = {
  async getActivities(search = "", sort = "") {
    const params = new URLSearchParams();
    if (search) params.append("search", search);
    if (sort) params.append("sort", sort);

    const response = await api.get(`/activities?${params.toString()}`);
    return response.data;
  },
};
>>>>>>> 54ec64d268892d113ae16f829891470804b4c3fd
