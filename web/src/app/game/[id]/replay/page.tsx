import { ReplayScreen } from "@/components/game/ReplayScreen";

export default async function ReplayPage(props: PageProps<"/game/[id]/replay">) {
  const { id } = await props.params;
  return <ReplayScreen gameId={id} />;
}
